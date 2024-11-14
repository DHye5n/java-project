document.getElementById('login-button').addEventListener('click', async function() {

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (!username || !password) {
        alert("Username and password are required.");
        return;
    }

    const loginData = {
        username: username,
        password: password
    };

    try {

        const response = await fetch('/api/auth/sign-in', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (response.ok) {
            const result = await response.json();

            localStorage.setItem('access_token', result.data.accessToken);

            window.location.href = '/auth/dashboard';
        } else {
            const error = await response.json();
            alert(error.message || "Login failed. Please try again.");
        }
    } catch (error) {
        console.error("Error logging in:", error);
        alert("An error occurred while logging in.");
    }
});