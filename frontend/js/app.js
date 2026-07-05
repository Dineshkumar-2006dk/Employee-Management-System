// API Base URL (loads from localStorage for cloud routing, falls back to host origin)
const API_BASE = localStorage.getItem('ems_api_base_url') || window.location.origin;

// Global confirmation callback
let confirmCallback = null;

// Page initialization
document.addEventListener('DOMContentLoaded', () => {
    // 1. Determine which page we are on
    const isLoginPage = window.location.pathname.endsWith('login.html');
    const token = localStorage.getItem('ems_session_token');
    const username = localStorage.getItem('ems_session_user');

    if (isLoginPage) {
        setupLoginPage();
    } else {
        // Main App setup
        if (!token) {
            window.location.href = 'login.html';
            return;
        }

        // Set user profile
        if (username) {
            document.getElementById('user-display-name').textContent = username;
            document.getElementById('avatar-letters').textContent = username.substring(0, 2).toUpperCase();
        }

        setupMainPage();
    }
});

/* ==========================================================================
   TOAST NOTIFICATION HELPER
   ========================================================================== */
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    const icon = type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation';
    
    toast.innerHTML = `
        <i class="fa-solid ${icon}"></i>
        <div class="toast-message">${message}</div>
        <button class="toast-close" onclick="this.parentElement.remove()"><i class="fa-solid fa-xmark"></i></button>
    `;

    container.appendChild(toast);

    // Slide out and remove toast after 4 seconds
    setTimeout(() => {
        toast.classList.add('toast-out');
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 4000);
}

/* ==========================================================================
   LOGIN / REGISTER PAGE LOGIC
   ========================================================================== */
function setupLoginPage() {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    // Login Submission
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('login-submit-btn');
        submitBtn.classList.add('loading');

        const usernameInput = document.getElementById('login-username').value;
        const passwordInput = document.getElementById('login-password').value;

        try {
            const response = await fetch(`${API_BASE}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: usernameInput, password: passwordInput })
            });

            const result = await response.json();
            if (result.success) {
                showToast(result.message, 'success');
                localStorage.setItem('ems_session_token', result.data.token);
                localStorage.setItem('ems_session_user', result.data.user.username);
                
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1000);
            } else {
                showToast(result.message || 'Login failed', 'danger');
                submitBtn.classList.remove('loading');
            }
        } catch (err) {
            showToast('Network error connecting to authentication service.', 'danger');
            submitBtn.classList.remove('loading');
        }
    });

    // Registration Submission
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const submitBtn = document.getElementById('register-submit-btn');
        submitBtn.classList.add('loading');

        const usernameInput = document.getElementById('reg-username').value;
        const emailInput = document.getElementById('reg-email').value;
        const passwordInput = document.getElementById('reg-password').value;

        try {
            const response = await fetch(`${API_BASE}/api/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: usernameInput, email: emailInput, password: passwordInput, role: 'ADMIN' })
            });

            const result = await response.json();
            if (result.success) {
                showToast('Registration successful! You can now log in.', 'success');
                // Clear fields
                registerForm.reset();
                // Switch to login form
                setTimeout(() => {
                    document.getElementById('switch-to-login').click();
                    submitBtn.classList.remove('loading');
                }, 1500);
            } else {
                showToast(result.message || 'Registration failed', 'danger');
                submitBtn.classList.remove('loading');
            }
        } catch (err) {
            showToast('Network error during registration.', 'danger');
            submitBtn.classList.remove('loading');
        }
    });
}

/* ==========================================================================
   MAIN DASHBOARD APPLICATION LOGIC
   ========================================================================== */
function setupMainPage() {
    // 1. Load initial stats
    loadDashboardStats();

    // 2. Setup sidebar open/close toggle for mobile view
    const menuToggle = document.getElementById('menu-toggle');
    const sidebar = document.getElementById('app-sidebar');
    if (menuToggle && sidebar) {
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });

        // Close sidebar if user clicks outside of it on mobile
        document.addEventListener('click', (e) => {
            if (!sidebar.contains(e.target) && e.target !== menuToggle && !menuToggle.contains(e.target)) {
                sidebar.classList.remove('open');
            }
        });
    }

    // 3. Confirm Delete Yes Handler
    const confirmYesBtn = document.getElementById('confirm-yes-btn');
    if (confirmYesBtn) {
        confirmYesBtn.addEventListener('click', () => {
            if (confirmCallback) {
                confirmCallback();
                closeModal('confirm-modal');
            }
        });
    }
}

// Logout function
async function logoutAdmin() {
    try {
        await fetch(`${API_BASE}/api/auth/logout`, { method: 'POST' });
    } catch(e) {}
    
    localStorage.removeItem('ems_session_token');
    localStorage.removeItem('ems_session_user');
    showToast('Logged out successfully', 'success');
    setTimeout(() => {
        window.location.href = 'login.html';
    }, 800);
}

// Modal management
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Confirmation helper dialog
function showConfirmDialog(title, message, callback) {
    document.getElementById('confirm-title').textContent = title;
    document.getElementById('confirm-message').textContent = message;
    confirmCallback = callback;
    openModal('confirm-modal');
}

// View switching Router (SPA style)
function switchView(viewId) {
    // Hide all views
    const views = document.querySelectorAll('.content-view');
    views.forEach(v => {
        v.classList.remove('active');
    });

    // Remove active state from menu items
    const menuItems = document.querySelectorAll('.sidebar-menu li');
    menuItems.forEach(item => {
        item.classList.remove('active');
    });

    // Activate selected view and menu item
    const activeView = document.getElementById(viewId);
    if (activeView) {
        activeView.classList.add('active');
    }
    
    const menuItem = document.querySelector(`.sidebar-menu li[data-view="${viewId}"]`);
    if (menuItem) {
        menuItem.classList.add('active');
    }

    // Close mobile sidebar after navigation
    document.getElementById('app-sidebar').classList.remove('open');

    // Update Header Title & Load Data
    const viewTitle = document.getElementById('view-title');
    switch (viewId) {
        case 'dashboard-view':
            viewTitle.textContent = "Dashboard Overview";
            loadDashboardStats();
            break;
        case 'employees-view':
            viewTitle.textContent = "Employee Database";
            loadEmployees();
            break;
        case 'departments-view':
            viewTitle.textContent = "Department Hierarchy";
            loadDepartments();
            break;
        case 'salaries-view':
            viewTitle.textContent = "Payroll & Salary Logs";
            loadSalaries();
            break;
        case 'developer-view':
            viewTitle.textContent = "About Developer";
            break;
    }
}

/* ==========================================================================
   DASHBOARD STATS & COUNTER ANIMATION
   ========================================================================== */
async function loadDashboardStats() {
    try {
        // Fetch stats in parallel
        const [empRes, deptRes, payrollRes] = await Promise.all([
            fetch(`${API_BASE}/api/employees`),
            fetch(`${API_BASE}/api/departments`),
            fetch(`${API_BASE}/api/salaries/payroll`)
        ]);

        const employees = await empRes.json();
        const departments = await deptRes.json();
        const payrollValue = await payrollRes.json();

        // 1. Animate counters
        animateCounter('card-total-employees', employees.length);
        animateCounter('card-total-departments', departments.length);
        animateCounter('card-total-payroll', payrollValue, true);

        // 2. Populate Recent Employees Table (show latest 5)
        const recentTbody = document.getElementById('recent-employees-tbody');
        if (recentTbody) {
            recentTbody.innerHTML = '';
            
            // Sort by empId descending to get the newest
            const sortedEmps = [...employees].sort((a, b) => b.empId - a.empId).slice(0, 5);

            if (sortedEmps.length === 0) {
                recentTbody.innerHTML = `<tr><td colspan="3" class="empty-state">No employee records found.</td></tr>`;
            } else {
                sortedEmps.forEach(emp => {
                    recentTbody.innerHTML += `
                        <tr>
                            <td>
                                <div class="emp-table-profile">
                                    <div class="emp-table-avatar">${emp.name.substring(0, 2).toUpperCase()}</div>
                                    <div class="emp-table-details">
                                        <h5>${emp.name}</h5>
                                        <p>${emp.email}</p>
                                    </div>
                                </div>
                            </td>
                            <td>${emp.position}</td>
                            <td>${emp.joiningDate}</td>
                        </tr>
                    `;
                });
            }
        }

        // 3. Populate Quick Department List
        const quickDeptList = document.getElementById('quick-departments-list');
        if (quickDeptList) {
            quickDeptList.innerHTML = '';
            if (departments.length === 0) {
                quickDeptList.innerHTML = `<li class="empty-state">No departments configured.</li>`;
            } else {
                departments.slice(0, 5).forEach(dept => {
                    quickDeptList.innerHTML += `
                        <li class="quick-list-item">
                            <div class="quick-item-left">
                                <div class="quick-avatar"><i class="fa-solid fa-folder-open"></i></div>
                                <div class="quick-info">
                                    <h4>${dept.deptName}</h4>
                                    <p>Manager: ${dept.managerName || 'Unassigned'}</p>
                                </div>
                            </div>
                            <span class="quick-badge">${dept.employeeCount} Members</span>
                        </li>
                    `;
                });
            }
        }

    } catch (err) {
        console.error("Error loading dashboard statistics", err);
    }
}

// Numerical count animations
function animateCounter(elementId, targetVal, isCurrency = false) {
    const el = document.getElementById(elementId);
    if (!el) return;

    let start = 0;
    const duration = 1000; // ms
    const startTime = performance.now();

    function updateCounter(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        // Easing out quadratic
        const easeProgress = progress * (2 - progress);
        const currentVal = Math.floor(easeProgress * targetVal);

        if (isCurrency) {
            el.textContent = '$' + currentVal.toLocaleString('en-US');
        } else {
            el.textContent = currentVal;
        }

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        } else {
            if (isCurrency) {
                el.textContent = '$' + targetVal.toLocaleString('en-US');
            } else {
                el.textContent = targetVal;
            }
        }
    }

    requestAnimationFrame(updateCounter);
}

/* ==========================================================================
   EMPLOYEE MODULE CRUD OPERATIONS
   ========================================================================== */
async function loadEmployees(query = '') {
    const tbody = document.getElementById('employees-tbody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Loading employees...</td></tr>`;

    try {
        const url = query ? `${API_BASE}/api/employees?search=${encodeURIComponent(query)}` : `${API_BASE}/api/employees`;
        const res = await fetch(url);
        const list = await res.json();

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="7" class="empty-state">
                        <i class="fa-solid fa-users-slash"></i>
                        <h3>No Employees Found</h3>
                        <p>No matching employee profiles match your criteria.</p>
                    </td>
                </tr>`;
            return;
        }

        list.forEach(emp => {
            tbody.innerHTML += `
                <tr>
                    <td>${emp.empId}</td>
                    <td>
                        <div class="emp-table-profile">
                            <div class="emp-table-avatar">${emp.name.substring(0, 2).toUpperCase()}</div>
                            <div class="emp-table-details">
                                <h5>${emp.name}</h5>
                                <p>${emp.email}</p>
                            </div>
                        </div>
                    </td>
                    <td>${emp.phone}</td>
                    <td><span class="quick-badge" style="background: rgba(168, 85, 247, 0.1); color: var(--accent-color); border: 1px solid rgba(168, 85, 247, 0.15)">${emp.deptName}</span></td>
                    <td>${emp.position}</td>
                    <td>${emp.joiningDate}</td>
                    <td class="actions-cell">
                        <button class="btn-icon btn-icon-edit" onclick="openEditEmployeeModal(${emp.empId})" title="Edit details"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn-icon btn-icon-assign" onclick="openSalaryModal(${emp.empId}, '${emp.name}')" title="Configure salary"><i class="fa-solid fa-wallet"></i></button>
                        <button class="btn-icon btn-icon-delete" onclick="triggerDeleteEmployee(${emp.empId})" title="Delete profile"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `;
        });

    } catch (err) {
        showToast('Error loading employee lists.', 'danger');
    }
}

// Search debounce
let searchTimeout;
function handleEmployeeSearch() {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        const query = document.getElementById('employee-search').value;
        loadEmployees(query);
    }, 300);
}

// Populating department options into selection dropdowns
async function loadDepartmentDropdown(selectedValue = '') {
    const dropdown = document.getElementById('emp-department');
    if (!dropdown) return;

    dropdown.innerHTML = '<option value="">Select Department</option>';

    try {
        const res = await fetch(`${API_BASE}/api/departments`);
        const depts = await res.json();
        depts.forEach(d => {
            const isSelected = selectedValue == d.deptId ? 'selected' : '';
            dropdown.innerHTML += `<option value="${d.deptId}" ${isSelected}>${d.deptName}</option>`;
        });
    } catch (err) {
        console.error('Error fetching department dropdowns', err);
    }
}

// Open modals for employee addition
function openAddEmployeeModal() {
    document.getElementById('employee-modal-title').textContent = 'Add New Employee';
    document.getElementById('employee-form').reset();
    document.getElementById('emp-form-id').value = '';
    loadDepartmentDropdown();
    openModal('employee-modal');
}

// Open modals for employee edits
async function openEditEmployeeModal(empId) {
    document.getElementById('employee-modal-title').textContent = 'Edit Employee Profile';
    
    try {
        const res = await fetch(`${API_BASE}/api/employees/${empId}`);
        const emp = await res.json();

        document.getElementById('emp-form-id').value = emp.empId;
        document.getElementById('emp-name').value = emp.name;
        document.getElementById('emp-email').value = emp.email;
        document.getElementById('emp-phone').value = emp.phone;
        document.getElementById('emp-position').value = emp.position;
        document.getElementById('emp-date').value = emp.joiningDate;

        await loadDepartmentDropdown(emp.deptId);
        openModal('employee-modal');
    } catch (err) {
        showToast('Error retrieving employee record.', 'danger');
    }
}

// Save or Update employee details
async function saveEmployee(event) {
    event.preventDefault();
    const empId = document.getElementById('emp-form-id').value;
    
    const payload = {
        name: document.getElementById('emp-name').value,
        email: document.getElementById('emp-email').value,
        phone: document.getElementById('emp-phone').value,
        deptId: document.getElementById('emp-department').value ? parseInt(document.getElementById('emp-department').value) : null,
        position: document.getElementById('emp-position').value,
        joiningDate: document.getElementById('emp-date').value
    };

    const url = empId ? `${API_BASE}/api/employees/${empId}` : `${API_BASE}/api/employees`;
    const method = empId ? 'PUT' : 'POST';

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const result = await res.json();
        if (result.success) {
            showToast(result.message, 'success');
            closeModal('employee-modal');
            loadEmployees();
        } else {
            showToast(result.message, 'danger');
        }
    } catch (err) {
        showToast('Error saving employee profile information.', 'danger');
    }
}

// Trigger employee deletes
function triggerDeleteEmployee(empId) {
    showConfirmDialog(
        "Confirm Deletion",
        "Are you sure you want to delete this employee? Their entire profile and salary records will be deleted permanently.",
        async () => {
            try {
                const res = await fetch(`${API_BASE}/api/employees/${empId}`, { method: 'DELETE' });
                const result = await res.json();
                if (result.success) {
                    showToast(result.message, 'success');
                    loadEmployees();
                } else {
                    showToast(result.message, 'danger');
                }
            } catch (err) {
                showToast('Error removing employee record.', 'danger');
            }
        }
    );
}

/* ==========================================================================
   DEPARTMENT MODULE CRUD OPERATIONS
   ========================================================================== */
async function loadDepartments() {
    const tbody = document.getElementById('departments-tbody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Loading departments...</td></tr>`;

    try {
        const res = await fetch(`${API_BASE}/api/departments`);
        const list = await res.json();

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-state">
                        <i class="fa-solid fa-sitemap"></i>
                        <h3>No Departments Configured</h3>
                        <p>Get started by adding a department structure.</p>
                    </td>
                </tr>`;
            return;
        }

        list.forEach(dept => {
            tbody.innerHTML += `
                <tr>
                    <td>${dept.deptId}</td>
                    <td><strong>${dept.deptName}</strong></td>
                    <td>${dept.managerName || 'Unassigned'}</td>
                    <td><span class="quick-badge">${dept.employeeCount} Employees</span></td>
                    <td class="actions-cell">
                        <button class="btn-icon btn-icon-edit" onclick="openEditDepartmentModal(${dept.deptId})" title="Edit department"><i class="fa-solid fa-pen"></i></button>
                        <button class="btn-icon btn-icon-delete" onclick="triggerDeleteDepartment(${dept.deptId})" title="Delete department"><i class="fa-solid fa-trash"></i></button>
                    </td>
                </tr>
            `;
        });
    } catch (err) {
        showToast('Error loading departments details.', 'danger');
    }
}

function openAddDepartmentModal() {
    document.getElementById('department-modal-title').textContent = 'Add Department';
    document.getElementById('department-form').reset();
    document.getElementById('dept-form-id').value = '';
    openModal('department-modal');
}

async function openEditDepartmentModal(deptId) {
    document.getElementById('department-modal-title').textContent = 'Edit Department';
    
    try {
        const res = await fetch(`${API_BASE}/api/departments/${deptId}`);
        const dept = await res.json();

        document.getElementById('dept-form-id').value = dept.deptId;
        document.getElementById('dept-name').value = dept.deptName;
        document.getElementById('dept-manager').value = dept.managerName;

        openModal('department-modal');
    } catch (err) {
        showToast('Error fetching department details.', 'danger');
    }
}

async function saveDepartment(event) {
    event.preventDefault();
    const deptId = document.getElementById('dept-form-id').value;
    
    const payload = {
        deptName: document.getElementById('dept-name').value,
        managerName: document.getElementById('dept-manager').value
    };

    const url = deptId ? `${API_BASE}/api/departments/${deptId}` : `${API_BASE}/api/departments`;
    const method = deptId ? 'PUT' : 'POST';

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const result = await res.json();
        if (result.success) {
            showToast(result.message, 'success');
            closeModal('department-modal');
            loadDepartments();
        } else {
            showToast(result.message, 'danger');
        }
    } catch (err) {
        showToast('Error saving department details.', 'danger');
    }
}

function triggerDeleteDepartment(deptId) {
    showConfirmDialog(
        "Delete Department",
        "Are you sure you want to delete this department? Employees assigned to it will be set to 'Unassigned' automatically.",
        async () => {
            try {
                const res = await fetch(`${API_BASE}/api/departments/${deptId}`, { method: 'DELETE' });
                const result = await res.json();
                if (result.success) {
                    showToast(result.message, 'success');
                    loadDepartments();
                } else {
                    showToast(result.message, 'danger');
                }
            } catch (err) {
                showToast('Error removing department record.', 'danger');
            }
        }
    );
}

/* ==========================================================================
   SALARY MODULE LOGIC
   ========================================================================== */
async function loadSalaries() {
    const tbody = document.getElementById('salaries-tbody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="6" class="empty-state">Loading salary logs...</td></tr>`;

    try {
        const res = await fetch(`${API_BASE}/api/salaries`);
        const list = await res.json();

        tbody.innerHTML = '';
        if (list.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="empty-state">
                        <i class="fa-solid fa-money-check-dollar"></i>
                        <h3>No Salaries Configured</h3>
                        <p>Go to the Employees page and configure salaries for staff.</p>
                    </td>
                </tr>`;
            return;
        }

        list.forEach(sal => {
            tbody.innerHTML += `
                <tr>
                    <td>${sal.salaryId}</td>
                    <td><strong>${sal.employeeName}</strong> (ID: ${sal.empId})</td>
                    <td>$${sal.basicSalary.toLocaleString('en-US', {minimumFractionDigits: 2})}</td>
                    <td>$${sal.bonus.toLocaleString('en-US', {minimumFractionDigits: 2})}</td>
                    <td><strong style="color: var(--success)">$${sal.totalSalary.toLocaleString('en-US', {minimumFractionDigits: 2})}</strong></td>
                    <td class="actions-cell">
                        <button class="btn-icon btn-icon-edit" onclick="openSalaryModal(${sal.empId}, '${sal.employeeName}')" title="Update Salary"><i class="fa-solid fa-pen-to-square"></i></button>
                    </td>
                </tr>
            `;
        });
    } catch (err) {
        showToast('Error loading salary configurations.', 'danger');
    }
}

// Open salary edit/set modal
async function openSalaryModal(empId, employeeName) {
    document.getElementById('salary-form').reset();
    document.getElementById('salary-form-emp-id').value = empId;
    document.getElementById('salary-emp-name').value = employeeName;
    document.getElementById('salary-form-id').value = '';

    try {
        const res = await fetch(`${API_BASE}/api/salaries/employee/${empId}`);
        if (res.ok) {
            const sal = await res.json();
            document.getElementById('salary-form-id').value = sal.salaryId;
            document.getElementById('salary-basic').value = sal.basicSalary;
            document.getElementById('salary-bonus').value = sal.bonus;
        } else {
            // Default blank fields for a new record setup
            document.getElementById('salary-basic').value = '0';
            document.getElementById('salary-bonus').value = '0';
        }
        calculateTotalSalaryPreview();
        openModal('salary-modal');
    } catch (err) {
        // Fallback for new record creation
        document.getElementById('salary-basic').value = '0';
        document.getElementById('salary-bonus').value = '0';
        calculateTotalSalaryPreview();
        openModal('salary-modal');
    }
}

// Input dynamic updates
function calculateTotalSalaryPreview() {
    const basic = parseFloat(document.getElementById('salary-basic').value) || 0;
    const bonus = parseFloat(document.getElementById('salary-bonus').value) || 0;
    const sum = basic + bonus;
    document.getElementById('salary-total-preview').value = sum.toLocaleString('en-US', {minimumFractionDigits: 2});
}

// Save/Update salary record
async function saveSalary(event) {
    event.preventDefault();
    const salaryId = document.getElementById('salary-form-id').value;
    const empId = parseInt(document.getElementById('salary-form-emp-id').value);
    
    const payload = {
        salaryId: salaryId ? parseInt(salaryId) : null,
        empId: empId,
        basicSalary: parseFloat(document.getElementById('salary-basic').value) || 0.0,
        bonus: parseFloat(document.getElementById('salary-bonus').value) || 0.0
    };

    // The backend uses saveSalary POST endpoint that supports updates by saving existing employee records
    const url = `${API_BASE}/api/salaries`;

    try {
        const res = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const result = await res.json();
        if (result.success) {
            showToast(result.message, 'success');
            closeModal('salary-modal');
            
            // Check which page is currently active and refresh appropriate content
            const activeView = document.querySelector('.content-view.active');
            if (activeView.id === 'salaries-view') {
                loadSalaries();
            } else if (activeView.id === 'employees-view') {
                loadEmployees();
            } else {
                loadDashboardStats();
            }
        } else {
            showToast(result.message, 'danger');
        }
    } catch (err) {
        showToast('Error saving salary configuration details.', 'danger');
    }
}
