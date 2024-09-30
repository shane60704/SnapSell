document.addEventListener('DOMContentLoaded', function(){
    // Smooth Scroll for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            document.querySelector(this.getAttribute('href')).scrollIntoView({
                behavior: 'smooth'
            });
        });
    });

    // 登入顯示當前用戶名字
    document.getElementById('user-name').textContent="嗨,"+localStorage.getItem("userName");

    // 使用者頭像綁定監聽事件(顯示隱藏視窗)
    document.querySelector('.icon-user').addEventListener('click', function() {
        console.log("點擊");
        var infoWindow = document.querySelector('header .user-info .info-window');
        if (infoWindow.style.display === 'block') {
            infoWindow.style.display = 'none';
        } else {
            infoWindow.style.display = 'block';
        }
    });

    // 綁定導向我的合約按鈕
    document.getElementById('my-contract-btn').addEventListener('click',function (){
        window.location.href="my-contract.html";
    });

    // 綁定登出功能
    document.getElementById('logout-btn').addEventListener('click',function (){
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        window.location.href="login.html";
    });

    document.querySelector('.logo').addEventListener('click',function (){
        window.location.href="index.html";
    });
});