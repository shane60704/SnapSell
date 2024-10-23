document.addEventListener('DOMContentLoaded', async function () {
    const jwtToken = localStorage.getItem('jwtToken');
    if (jwtToken) {
        const isValid = await solveJwtToken(jwtToken);
        if (!isValid) {
            window.location.href = 'login.html';
        }
    } else {
        window.location.href = 'login.html';
    }
});

async function solveJwtToken(jwtToken) {
    try {
        const response = await fetch(`/api/1.0/user/solve-jwt?token=${jwtToken}`, {
            method: 'GET'
        });

        if (!response.ok) {
            throw new Error("Token is invalid or an error occurred during the validation.");
        }
        return true;
    } catch (error) {
        localStorage.removeItem("jwtToken");
        localStorage.removeItem("userEmail");
        localStorage.removeItem("userId");
        localStorage.removeItem("userImage");
        localStorage.removeItem("userName");
        localStorage.removeItem("UserImage");
        return false;
    }
}