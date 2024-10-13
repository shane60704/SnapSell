document.addEventListener('DOMContentLoaded', function () {
    const jwtToken = localStorage.getItem('jwtToken');
    if (jwtToken) {
        solveJwtToken(jwtToken);
    }
    window.location.href = 'login.html';
    return;
});

function solveJwtToken(jwtToken){
    fetch(`/api/1.0/user/solve-jwt?token=${jwtToken}`, {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Token is invalid or an error occurred during the validation.");
            }
        })
        .catch(error => {
            localStorage.removeItem("jwtToken");
            localStorage.removeItem("userEmail");
            localStorage.removeItem("userId");
            localStorage.removeItem("userImage");
            localStorage.removeItem("userName");
            localStorage.removeItem("UserImage");
            window.location.href = 'login.html';
        })
}