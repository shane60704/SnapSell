document.addEventListener('DOMContentLoaded', function(){

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

    // 載入當前使用者的頭像
    document.querySelector('.icon-user').src = `${localStorage.getItem("userImage")}`;

    // 使用者頭像綁定監聽事件(顯示隱藏視窗)
    document.querySelector('.icon-user').addEventListener('click', function() {
        var infoWindow = document.querySelector('header .user-info .info-window');
        if (infoWindow.style.display === 'block') {
            infoWindow.style.display = 'none';
        } else {
            infoWindow.style.display = 'block';
        }
    });

    // 合約紀錄
    document.getElementById('my-contract-btn').addEventListener('click',function (){
        window.location.href = "my-contract.html";
    });

    // 直播紀錄
    document.getElementById('my-livestream-btn').addEventListener('click',function (){
        window.location.href = "live-stream-records";
    });

    // 綁定登出功能
    document.getElementById('logout-btn').addEventListener('click',function (){
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        localStorage.removeItem('userImage');
        window.location.href="login.html";
    });

    document.querySelector('.header-logo').addEventListener('click',function (){
        window.location.href="index.html";
    });
});