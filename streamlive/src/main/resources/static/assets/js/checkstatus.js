if(!localStorage.getItem('userId')){
    document.querySelector('.hero-btn-commission').style.display = "none";
    document.querySelector('.hero-btn-agent').style.display = "none";
}else{
    document.querySelector('.hero-btn-login').style.display = "none";
}

const loginPrompt = document.getElementById('snapsellLoginPrompt');
const loginConfirm = document.getElementById('snapsellLoginConfirm');
const loginCancel = document.getElementById('snapsellLoginCancel');

function isLoggedIn() {
    return !!localStorage.getItem("userId");
}

function showLoginPrompt() {
    loginPrompt.style.display = 'flex';
}

function hideLoginPrompt() {
    loginPrompt.style.display = 'none';
}

document.querySelectorAll('nav ul li a').forEach(link => {
    link.addEventListener('click', function(e) {
        if (!isLoggedIn()) {
            e.preventDefault();
            showLoginPrompt();
        }
    });
});

loginConfirm.addEventListener('click', function() {
    window.location.href = 'login.html';
});

loginCancel.addEventListener('click', hideLoginPrompt);