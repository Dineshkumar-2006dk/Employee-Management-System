# Employee Management System (EMS) - Cloud Deployment Edition

A complete, modern full-stack web application built for organization administrators to manage employee databases, departments, and payroll details digitally. This project is structured as a **Mono-repo** and pre-configured for complete cloud deployment:
- **Backend API**: Hosted on **Render** (Java + Spring Boot + PostgreSQL Cloud Database).
- **Frontend Website**: Hosted on **Vercel** or **Netlify** (HTML5 + CSS3 + Vanilla JavaScript SPA).
- **Database**: Cloud-hosted **PostgreSQL** (e.g., Supabase, Neon, or Render PostgreSQL).

No local database or software installation is required! It is designed to work as a real public website.

Created as a portfolio project by **DINESHKUMAR S** (Electronics and Communication Engineering, Mailam Engineering College).

---

## Project Structure
```text
Employee Management System/
│
├── backend/               # Spring Boot REST API
│   ├── pom.xml            # Maven Configuration (PostgreSQL driver)
│   └── src/               # Java Service Layer, Controllers, and Entities
│
├── frontend/              # Frontend static web application (SPA)
│   ├── login.html         # Login / Registration & API gateway config
│   ├── index.html         # Admin dashboard pages
│   ├── css/style.css      # Premium dark space-slate styling
│   └── js/app.js          # Dynamic fetch calls & state management
│
└── .gitignore             # Git ignore patterns
```

---

## Deployment Walkthrough

### Step 1: Provision a PostgreSQL Cloud Database
You can use any cloud PostgreSQL provider (such as [Neon.tech](https://neon.tech), [Supabase](https://supabase.com), or Render's built-in PostgreSQL service).
1. Sign up for a free account.
2. Create a new database project (e.g., named `employee_management`).
3. Copy the database connection details:
   - **Host/URL**: (e.g., `jdbc:postgresql://ep-cool-resonance-12345.aws.neon.tech/employee_management?sslmode=require`)
   - **Username**
   - **Password**

---

### Step 2: Deploy the Backend on Render
1. Push this entire project to a private or public GitHub repository.
2. Sign in to [Render](https://render.com).
3. Click **New** -> **Web Service**.
4. Connect your GitHub repository.
5. Configure the build parameters:
   - **Name**: `employee-management-backend`
   - **Environment**: `Docker` or `Java` (Select **Java** / **Maven** runtime)
   - **Root Directory**: `backend`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/employee-management-system-0.0.1-SNAPSHOT.jar`
6. Add the following **Environment Variables** in Render's dashboard under the "Environment" tab:
   - `DATABASE_URL`: Your cloud PostgreSQL JDBC connection URL (e.g., `jdbc:postgresql://<host>/<database_name>`).
   - `DATABASE_USERNAME`: Your database username.
   - `DATABASE_PASSWORD`: Your database password.
   - `PORT`: `8080` (Render will map this port automatically).
7. Deploy the Web Service. Once active, copy the live URL provided by Render (e.g., `https://employee-management-system-tz8t.onrender.com`).

---

### Step 3: Deploy the Frontend on Vercel or Netlify
1. Create a new site on **Vercel** or **Netlify**.
2. Connect your GitHub repository.
3. Configure the build settings:
   - **Root Directory**: `frontend` (Or set it as the build folder)
   - **Build Command**: Leave blank (no build tool needed, it's vanilla HTML/JS!)
   - **Publish Directory**: `.` (which points to the `frontend` folder)
4. Deploy the site. Copy your deployed frontend URL (e.g., `https://employee-management-system-xi-mauve.vercel.app`).

---

### Step 4: Connecting the Frontend to the Deployed Backend
To prevent hardcoded links and allow you to test both local and production environments instantly, a **Settings panel** is integrated directly into the login screen:
1. Open your deployed Vercel/Netlify website link in the browser.
2. Click the **Gear icon (⚙️)** in the top right corner of the glassmorphism login card.
3. Enter your deployed Render API URL (e.g., `https://employee-management-system-tz8t.onrender.com`).
4. Click the checkmark to save.
5. Register a new Admin, log in, and begin managing your company data! The frontend will store this URL in your browser's `localStorage` and route all API calls (Employees, Departments, Salaries) directly to your Render backend.

---

## REST API Documentation

### 1. Authentication
- `POST /api/auth/register` - Registers a new admin (accepts JSON with `username`, `email`, and `password`).
- `POST /api/auth/login` - Validates credentials (using SHA-256 password comparisons) and logs in the admin.
- `POST /api/auth/logout` - Discards active session markers.

### 2. Employees
- `GET /api/employees` - Fetches all employees.
- `GET /api/employees?search={nameOrId}` - Searches employee records by full name or numeric ID.
- `POST /api/employees` - Adds a new employee record.
- `PUT /api/employees/{id}` - Updates employee data.
- `DELETE /api/employees/{id}` - Removes employee and deletes their corresponding salary credentials to preserve foreign-key mappings.

### 3. Departments
- `GET /api/departments` - Lists all departments and dynamically computes the employee count for each.
- `POST /api/departments` - Creates a new department.
- `PUT /api/departments/{id}` - Updates department manager name or department name.
- `DELETE /api/departments/{id}` - Deletes a department and automatically resets associated employees' departments to "Unassigned".

### 4. Salaries
- `GET /api/salaries` - Lists all payroll logs.
- `GET /api/salaries/employee/{empId}` - Gets salary record for a specific employee.
- `POST /api/salaries` - Saves or updates basic salary and bonus details. Automatically computes `totalSalary = basicSalary + bonus` inside the JPA service.
- `GET /api/salaries/payroll` - Computes total organizational payroll expense.
