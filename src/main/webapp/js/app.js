// Get the base URL dynamically
function getBaseUrl() {
    const path = window.location.pathname;
    const contextPath = path.substring(0, path.indexOf('/', 1));
    return contextPath || '';
}

const baseUrl = getBaseUrl();
console.log('Base URL:', baseUrl);

// Notification function
function showMessage(message, type = 'info') {
    const existing = document.querySelector('.notification');
    if (existing) existing.remove();

    const msgDiv = document.createElement('div');
    msgDiv.className = 'notification';
    msgDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        background: ${type === 'error' ? '#f56565' : type === 'success' ? '#48bb78' : '#667eea'};
        color: white;
        border-radius: 5px;
        z-index: 1000;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
        animation: slideIn 0.3s ease;
    `;
    msgDiv.textContent = message;
    document.body.appendChild(msgDiv);

    setTimeout(() => {
        msgDiv.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => msgDiv.remove(), 300);
    }, 3000);
}

// Add animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

// Validate registration
function validateRegister(form) {
    if (form.password.value !== form.confirmPassword.value) {
        showMessage('Passwords do not match!', 'error');
        return false;
    }
    if (form.password.value.length < 6) {
        showMessage('Password must be at least 6 characters!', 'error');
        return false;
    }
    return true;
}

// Handle registration
document.addEventListener('DOMContentLoaded', function() {
    // Register form
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            if (!validateRegister(this)) return;

            const formData = {
                username: this.username.value,
                email: this.email.value,
                password: this.password.value,
                fullName: this.fullName.value,
                age: this.age.value,
                gender: this.gender.value
            };

            try {
                const response = await fetch(baseUrl + '/api/register', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(formData)
                });

                const data = await response.json();

                if (data.success) {
                    showMessage('Registration successful!', 'success');
                    setTimeout(() => window.location.href = baseUrl + '/login.html', 1500);
                } else {
                    showMessage(data.message, 'error');
                }
            } catch (error) {
                showMessage('Error: ' + error.message, 'error');
            }
        });
    }

    // Login form
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            try {
                const response = await fetch(baseUrl + '/api/login', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({
                        username: this.username.value,
                        password: this.password.value
                    })
                });

                const data = await response.json();

                if (data.success) {
                    showMessage('Login successful!', 'success');
                    setTimeout(() => window.location.href = baseUrl + '/dashboard.html', 1500);
                } else {
                    showMessage(data.message, 'error');
                }
            } catch (error) {
                showMessage('Error: ' + error.message, 'error');
            }
        });
    }

    // Logout button
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async function() {
            await fetch(baseUrl + '/api/logout', {method: 'POST'});
            window.location.href = baseUrl + '/login.html';
        });
    }

    // Load dashboard data
    if (window.location.pathname.includes('dashboard.html')) {
        checkLoginAndLoadDashboard();
    }
});

// Check login and load dashboard
async function checkLoginAndLoadDashboard() {
    try {
        const response = await fetch(baseUrl + '/api/login');
        const data = await response.json();

        if (!data.loggedIn) {
            window.location.href = baseUrl + '/login.html';
            return;
        }

        document.getElementById('userName').textContent = data.username;
        loadDashboardData();
    } catch (error) {
        showMessage('Error checking login status', 'error');
    }
}

// Load dashboard data
async function loadDashboardData() {
    try {
        const response = await fetch(baseUrl + '/api/health/');
        const data = await response.json();

        if (data.success) {
            displayHealthData(data.data);
        } else {
            showMessage(data.message, 'error');
        }
    } catch (error) {
        showMessage('Error loading data: ' + error.message, 'error');
    }
}

// Display health data
function displayHealthData(data) {
    const tableBody = document.getElementById('healthDataTable');
    if (!tableBody) return;

    if (!data || data.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="8" style="text-align: center;">No health data found. Click "Add New Record" to add your first health record.</td></tr>';
        return;
    }

    let html = '';
    data.forEach(item => {
        html += `
            <tr>
                <td>${item.date}</td>
                <td>${item.weight} kg</td>
                <td>${item.height} cm</td>
                <td>${item.bloodPressureSystolic}/${item.bloodPressureDiastolic}</td>
                <td>${item.heartRate}</td>
                <td>${item.steps}</td>
                <td>${item.sleepHours}</td>
                <td>
                    <button onclick="editData(${item.id})" class="btn-small" style="background: #3498db;">Edit</button>
                    <button onclick="deleteData(${item.id})" class="btn-small btn-danger">Delete</button>
                </td>
            </tr>
        `;
    });
    tableBody.innerHTML = html;
}

// Edit data function
async function editData(id) {
    try {
        const response = await fetch(baseUrl + '/api/health/' + id);
        const data = await response.json();

        if (data.success && data.data) {
            const record = data.data;

            // Populate the form
            document.getElementById('dataId').value = record.id;
            document.getElementById('date').value = record.date;
            document.getElementById('weight').value = record.weight;
            document.getElementById('height').value = record.height;
            document.getElementById('bpSystolic').value = record.bloodPressureSystolic;
            document.getElementById('bpDiastolic').value = record.bloodPressureDiastolic;
            document.getElementById('heartRate').value = record.heartRate;
            document.getElementById('steps').value = record.steps;
            document.getElementById('sleepHours').value = record.sleepHours;
            document.getElementById('notes').value = record.notes || '';

            // Change modal title and button
            document.getElementById('modalTitle').textContent = 'Edit Health Record';
            document.getElementById('saveButton').textContent = 'Update Record';

            // Open the modal
            openModal();
        } else {
            showMessage('Error loading record', 'error');
        }
    } catch (error) {
        showMessage('Error: ' + error.message, 'error');
    }
}

// Save health data (for both add and update)
async function saveHealthData() {
    const form = document.getElementById('healthForm');
    const dataId = document.getElementById('dataId').value;

    const formData = {
        date: form.date.value,
        weight: parseFloat(form.weight.value),
        height: parseFloat(form.height.value),
        bpSystolic: parseInt(form.bpSystolic.value),
        bpDiastolic: parseInt(form.bpDiastolic.value),
        heartRate: parseInt(form.heartRate.value),
        steps: parseInt(form.steps.value),
        sleepHours: parseFloat(form.sleepHours.value),
        notes: form.notes.value || ''
    };

    try {
        let url = baseUrl + '/api/health/';
        let method = 'POST';

        if (dataId) {
            url = baseUrl + '/api/health/' + dataId;
            method = 'PUT';
        }

        const response = await fetch(url, {
            method: method,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(formData)
        });

        const data = await response.json();

        if (data.success) {
            showMessage(dataId ? 'Record updated!' : 'Data saved!', 'success');
            closeModal();
            loadDashboardData();
        } else {
            showMessage(data.message, 'error');
        }
    } catch (error) {
        showMessage('Error saving data: ' + error.message, 'error');
    }
}

// Delete health data
async function deleteData(id) {
    if (!confirm('Are you sure you want to delete this record?')) return;

    try {
        const response = await fetch(baseUrl + '/api/health/' + id, {
            method: 'DELETE'
        });

        const data = await response.json();

        if (data.success) {
            showMessage('Record deleted!', 'success');
            loadDashboardData();
        } else {
            showMessage(data.message, 'error');
        }
    } catch (error) {
        showMessage('Error deleting: ' + error.message, 'error');
    }
}

// Modal functions
function openModal() {
    document.getElementById('healthModal').style.display = 'block';

    // Set today's date if adding new
    if (!document.getElementById('dataId').value) {
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('date').value = today;
    }
}

function closeModal() {
    document.getElementById('healthModal').style.display = 'none';
    document.getElementById('healthForm').reset();
    document.getElementById('dataId').value = '';
    document.getElementById('modalTitle').textContent = 'Add Health Record';
    document.getElementById('saveButton').textContent = 'Save Record';
}