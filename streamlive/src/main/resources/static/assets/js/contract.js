document.addEventListener('DOMContentLoaded', function () {

    // 初始隱藏合約彈出視窗
    document.querySelector('.contract-signature-container').style.display = 'none';

    // 取得下拉選單和隱藏欄位的元素
    const productSelect = document.getElementById('productName');
    const productIdInput = document.getElementById('productId');

    // 使用 fetch API 來從後端取得商品數據
    fetch(`/api/1.0/product/undelgated?userId=${localStorage.getItem('userId')}`)
        .then(response => response.json())
        .then(data => {
            const products = data.data;

            // 將每個商品加入到下拉選單中
            products.forEach(product => {
                const option = document.createElement('option');
                option.value = product.id; // 將商品的 id 作為選項的值
                option.textContent = product.name; // 將商品的名稱顯示出來
                productSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error fetching product data:', error);
        });


    // 當使用者選擇商品時，更新隱藏欄位的值
    productSelect.addEventListener('change', function () {
        productIdInput.value = productSelect.value; // 將選中的商品 ID 設置到隱藏欄位中
    });
})

document.querySelector('.chatRoomHidden').addEventListener('click',()=>{
    document.querySelector('.chat-container').style.display = "none";
    document.querySelector('.floating-circle').style.display = "flex"
});

document.querySelector('.floating-circle').addEventListener('click',()=>{
    document.querySelector('.floating-circle').style.display = "none"
    document.querySelector('.chat-container').style.display = "flex";
});

document.querySelector('.contract-signature-container').style.display="none";
document.querySelector('.contract-signature-block').style.display="none";
document.querySelector('.signature-block-A').style.display="none";
document.querySelector('.signature-block-B').style.display="none";
document.querySelector('.signature-A').style.display="none";
document.querySelector('.signature-B').style.display="none";
document.querySelector('.contract-signature-send-button').style.display='none';
document.getElementById('sendAcceptBtn').style.display='none';
document.getElementById('sendRejectBtn').style.display='none';

document.getElementById('contract-closeBtn').addEventListener('click', function () {
    document.querySelector('.contract-signature-container').style.display = 'none';
});
document.getElementById('createContractBtn').addEventListener('click',function () {
    document.querySelector('.contract-signature-container').style.display = 'block';
});

// 生成合約
document.getElementById('generateContractBtn').addEventListener('click', function () {
    const contractData = {
        clientName: document.getElementById('clientName').value,
        sellerName: document.getElementById('sellerName').value,
        productName: document.getElementById('productName').value,
        salesPeriod: document.getElementById('salesPeriod').value,
        commissionRate: document.getElementById('commissionRate').value
    };
    const productSelect = document.getElementById('productName');
    const selectedProductName = productSelect.options[productSelect.selectedIndex].text;
    const contract_container = document.querySelector('.contract-content-container');
    const signature_container = document.querySelector('.contract-signature-block');

    const contract = `
        <div class="contract-content-container">
    <h3>委託銷售合約</h3>
    <hr>
    <p><strong>委託者</strong>：${contractData.clientName}</p>
    <p><strong>代理者</strong>：${contractData.sellerName}</p>
    <p><strong>商品名稱</strong>：${selectedProductName}</p>
    <p><strong>銷售期間</strong>：${contractData.salesPeriod}</p>
    <p><strong>佣金比例</strong>：代理者將從每筆銷售中獲得 ${contractData.commissionRate}% 的佣金。</p>
    <p>此合約由雙方協議達成，並自簽署日起生效。</p>
    <hr>
</div>
    `;
    document.getElementById('generated-contract').innerHTML = contract;
    document.querySelector('.contract-input-container').style.display = "none";
    document.getElementById('generated-contract').appendChild(signature_container);
    document.querySelector('.contract-signature-block').style.display="flex";
    document.querySelector('.signature-block-A').style.display="block";
});

// 初始化畫布
const signaturePadA = document.getElementById('signature-pad-a');
// 清除畫布按鈕
const clearSignatureA = document.getElementById('clearSignatureA');
// 將簽名保存變成圖片
const saveSignatureA = document.getElementById('saveSignatureA');
// 顯示照片的區域
const signatureImageA = document.getElementById('signatureImageA');
// 2D繪製
const ctxA = signaturePadA.getContext('2d');

// A是否可畫
let drawingA = false;

// 開始簽名
signaturePadA.addEventListener('mousedown', function(event) {
    drawingA = true;
    ctxA.beginPath();
    ctxA.moveTo(event.offsetX, event.offsetY);
});

// 畫簽名的過程
signaturePadA.addEventListener('mousemove', function(event) {
    if (drawingA) {
        ctxA.lineTo(event.offsetX, event.offsetY);
        ctxA.stroke();
    }
});

// 停止畫簽名
signaturePadA.addEventListener('mouseup', function() {
    drawingA = false;
});

saveSignatureA.addEventListener('click',function () {
    saveSignature(signaturePadA,signatureImageA);
    document.querySelector('.signature-A').style.display = "block";
})

// 初始化畫布
const signaturePadB = document.getElementById('signature-pad-b');
// 清除畫布按鈕
const clearSignatureB = document.getElementById('clearSignatureB');
// 將簽名保存變成圖片
const saveSignatureB = document.getElementById('saveSignatureB');
// 顯示照片的區域
const signatureImageB = document.getElementById('signatureImageB');
// 2D繪製
const ctxB = signaturePadB.getContext('2d');
// B是否可畫
let drawingB = false;

// 開始簽名
signaturePadB.addEventListener('mousedown', function(event) {
    drawingB = true;
    ctxB.beginPath();
    ctxB.moveTo(event.offsetX, event.offsetY);
});

// 畫簽名的過程
signaturePadB.addEventListener('mousemove', function(event) {
    if (drawingB) {
        ctxB.lineTo(event.offsetX, event.offsetY);
        ctxB.stroke();
    }
});

// 停止畫簽名
signaturePadB.addEventListener('mouseup', function() {
    drawingB = false;
});

saveSignatureB.addEventListener('click',function () {
    saveSignature(signaturePadB,signatureImageB);
    document.querySelector('.signature-B').style.display = "block";
})

// 保存簽名並清除畫布
function saveSignature(signaturePad,signatureImage) {
    let dataURL = signaturePad.toDataURL();
    signatureImage.src = dataURL; // 顯示簽名圖片
    document.querySelector('.contract-signature-block').style.display="none";
    if (signatureImage === signatureImageA){
        document.querySelector('.signature-B').style.display="none";
        document.querySelector('.contract-signature-send-button').style.display="block";
    }else{
        document.querySelector('.signature-A').style.display="none";
        document.getElementById('sendAcceptBtn').style.display='block';
        document.getElementById('sendRejectBtn').style.display='block';
    }
}
document.getElementById("clearSignatureB").addEventListener('click',function (){
    ctxB.clearRect(0, 0, signaturePadB.width, signaturePadB.height);
});

document.getElementById("clearSignatureA").addEventListener('click',function (){
    ctxA.clearRect(0, 0, signaturePadA.width, signaturePadA.height);
});

// 清除畫布
function clearSignature(ctx,signaturePad) {
    ctx.clearRect(0, 0, signaturePad.width, signaturePad.height);
}

let contractRoomId = null;

function connectWebSocketForContract(chatRoomId) {
    const socket = new SockJS('/contract'); // 確認 WebSocket 端點為 /contract
    const contractStompClient = Stomp.over(socket);
    contractStompClient.connect({}, function(frame) {
        console.log(chatRoomId);
        contractRoomId = chatRoomId;
        console.log('已連接到合約端點: ' + frame);
        const userId = getCurrentUserId(); // 確認是否有用戶 ID
        if (userId) {
            subscribeToNewContract(contractRoomId); // 訂閱合約頻道
        } else {
            alert("請先登入");
        }
    }, function(error) {
        console.error('合約連接失敗:', error);
        setTimeout(connectWebSocketForContract, 5000); // 5 秒後重試連接
    });
}

function subscribeToNewContract(contractRoomId){
    stompClient.subscribe(`/topic/contract/${contractRoomId}`,function(choose){
        console.log(`已經訂閱:/topic/contract/${contractRoomId}`)
        const result = JSON.parse(choose.body);
        console.log(result);
        if(result.senderId !== localStorage.getItem('userId')){
            switch(result.requestType){
                case "accept":
                    document.querySelector('.contract-signature-container').style.display = 'none';
                    showResultPopup('successPopup');
                    break;
                case "reject":
                    document.querySelector('.contract-signature-container').style.display = 'none';
                    showResultPopup('failurePopup');
                    break;
                case "request":
                    document.querySelector('.contract-signature-container').style.display = 'block';
                    // 得到請求，先清空合約填空處
                    document.querySelector('.contract-input-container').innerHTML='';
                    // 獲取合約內容
                    const contract = `
                <div class="contract-content-container">
                <h3>委託銷售合約</h3>
                <hr>
                <p id="clientName"><strong>委託者</strong>：${result.clientName}</p>
                <p id="sellerName"><strong>代理者</strong>：${result.sellerName}</p>
                <p id="productName"><strong>商品名稱</strong>：${result.productName}</p>
                <p id="salesPeriod"><strong>銷售期間</strong>：${result.salesPeriod}</p>
                <p id="commissionRate"><strong>佣金比例</strong>：代理者將從每筆銷售中獲得 ${result.commissionRate}% 的佣金。</p>
                <p>此合約由雙方協議達成，並自簽署日起生效。</p>
                <span id="clientId" style="display: none;">${result.clientId}</span>
                <span id="productId" style="display: none;">${result.productId}</span>
                <span id="signatureImage" style="display: none;">${result.signatureImage}</span>
                <hr>
                </div>
                `;
                    document.getElementById('generated-contract').innerHTML = contract;

                    document.querySelector('.contract-signature-block').style.display="flex";
                    document.querySelector('.signature-block-B').style.display="block";
                    break;
            }
        }else{
            switch (result.requestType){
                case "accept":
                    document.querySelector('.contract-signature-container').style.display = 'none';
                    showResultPopup('agentAgreePopup');
                    break;
                case "reject":
                    document.querySelector('.contract-signature-container').style.display = 'none';
                    showResultPopup('agentDisagreePopup');
                    break;
            }
        }
    });
}



// 發送訊息
document.getElementById("sendContractBtn").addEventListener("click", function() {

    const productSelect = document.getElementById('productName');
    const selectedProductName = productSelect.options[productSelect.selectedIndex].text;

    if (document.getElementById('currentChatRoomName').textContent) {
        const message = {
            requestType:"request",
            chatRoomId: contractRoomId,   // 當前聊天室 ID
            senderId: localStorage.getItem("userId"), // 發送者 ID (從 localStorage 獲取)
            clientId: localStorage.getItem("userId"),
            clientName: document.getElementById('clientName').value,
            sellerName: document.getElementById('sellerName').value,
            productName: selectedProductName,
            productId:document.getElementById('productId').value,
            salesPeriod: document.getElementById('salesPeriod').value,
            commissionRate: document.getElementById('commissionRate').value,
            signatureImage:signatureImageA.getAttribute('src'),
            timestamp: new Date().toISOString() // 當前時間
        };
        stompClient.send("/app/contract.sign", {}, JSON.stringify(message));// 透過 STOMP 發送訊息到後端
        document.getElementById('signature-preview').innerHTML=``;
        document.getElementById('generated-contract').innerHTML=`
            <div class = "contract-loading">
                <h3>合約已送出，等待代理者簽署中</h3>
                <div class="dots-container">
                <div class="dot"></div>
                <div class="dot"></div>
                <div class="dot"></div>
            </div>
            `;
        document.getElementById('sendContractBtn').style.display="none";
        document.getElementById('signature-preview').innerHTML=``;
    } else {
        alert("請選擇聊天室並輸入訊息");
    }
});

document.getElementById("sendAcceptBtn").addEventListener("click", function() {
    const message = {
        requestType:"accept",
        chatRoomId: contractRoomId,   // 當前聊天室 ID
        senderId: localStorage.getItem("userId"), // 發送者 ID
        clientId:document.getElementById('clientId').textContent,
        agentId:localStorage.getItem('userId'),
        clientName: document.getElementById('clientName').textContent,
        sellerName: document.getElementById('sellerName').textContent,
        productName: document.getElementById('productName').textContent,
        productId: document.getElementById('productId').textContent,
        salesPeriod: document.getElementById('salesPeriod').textContent,
        commissionRate: document.getElementById('commissionRate').textContent,
        signatureImage:document.getElementById('signatureImage').textContent,
        agentSignatureImage:signatureImageB.getAttribute('src'),
        timestamp: new Date().toISOString() // 當前時間
    };
    stompClient.send("/app/contract.sign", {}, JSON.stringify(message));// 透過 STOMP 發送訊息到後端
});


document.getElementById("sendRejectBtn").addEventListener("click", function() {
    const message = {
        requestType:"reject",
        chatRoomId: contractRoomId,   // 當前聊天室 ID
        clientId:document.getElementById('clientId').textContent,
        senderId: localStorage.getItem("userId"),                // 發送者 ID (從 localStorage 獲取)
        timestamp: new Date().toISOString() // 當前時間
    };
    stompClient.send("/app/contract.sign", {}, JSON.stringify(message));// 透過 STOMP 發送訊息到後端
});
