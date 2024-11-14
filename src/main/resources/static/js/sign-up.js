document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById('form');
    const btnJoin = document.getElementById('join-btn');
    const btnCheckEmail = document.getElementById('email-check-btn');
    const btnCheckId = document.getElementById('id-check-btn');
    const btnVerifyCode = document.getElementById('verify-code-btn');
    const passwordField = document.getElementById('password');
    const passwordMessage = document.getElementById('password-check-message');
    const passwordCheckField = document.getElementById('password-check');
    const passwordCheckMessage  = document.getElementById('password-check-message-confirm');
    const postFindButton  = document.getElementById('post-find-button');

    // Kakao 주소 찾기
    postFindButton.addEventListener('click', function () {
        new daum.Postcode({
            oncomplete: function(data) {
                var addr = '';
                if (data.userSelectedType === 'R') {
                    addr = data.roadAddress;
                } else {
                    addr = data.jibunAddress;
                }

                document.getElementById("zonecode").value = data.zonecode;
                document.getElementById("address").value = addr;
                document.getElementById("address-detail").focus();

            }
        }).open();
    });

    // 비밀번호 검증
    passwordField.addEventListener('input', function() {
        const password = passwordField.value;

        let message = '';
        let isValid = true;

        if (password.length < 8 || password.length > 20) {
            message = "비밀번호는 8자 이상 20자 이하여야 합니다.";
            isValid = false;
        }

        else if (!/(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])/.test(password)) {
            message = "비밀번호는 숫자, 소문자, 대문자를 각각 하나 이상 포함해야 합니다.";
            isValid = false;
        }

        if (isValid) {
            passwordMessage.textContent = "사용할 수 있는 비밀번호입니다.";
            passwordMessage.style.color = "green";
        } else {
            passwordMessage.textContent = message;
            passwordMessage.style.color = "red";
        }

        if (password) {
            passwordMessage.classList.remove('d-none');
        } else {
            passwordMessage.classList.add('d-none');
        }
    });

    // 비밀번호 확인 검증
    passwordCheckField.addEventListener('input', function() {
        const password = passwordField.value;
        const passwordCheck = passwordCheckField.value;
        const isValidPassword = password.length >= 8 && password.length <= 20 && /(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])/.test(password);
        let message = '';

        // Only validate password confirmation if password is valid
        if (isValidPassword) {
            if (password !== passwordCheck) {
                message = "비밀번호가 일치하지 않습니다.";
                passwordCheckMessage.style.color = "red";
            } else {
                message = "비밀번호가 일치합니다.";
                passwordCheckMessage.style.color = "green";
            }
            passwordCheckMessage.textContent = message;
            passwordCheckMessage.classList.remove('d-none');
        } else {
            passwordCheckMessage.classList.add('d-none');
        }

        if (!passwordCheck) {
            passwordCheckMessage.classList.add('d-none');
        }
    });

    // Handle form submission
    btnJoin.addEventListener('click', function() {
        if (!validateForm()) {
            return;
        }

        // Collect form data and convert to JSON
        const formData = new FormData(form);
        const jsonData = {};
        formData.forEach((value, key) => { jsonData[key] = value; });

        jsonData.verificationCode = document.getElementById('verification-code').value;

        console.log("Form data to send:", jsonData);

        fetch('/api/auth/sign-up', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(jsonData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert("회원가입이 완료되었습니다.");
                    window.location.href = "/auth/sign-in";
                } else {
                    alert("오류가 발생했습니다: " + data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("회원가입 중 오류가 발생했습니다.");
            });
    });

    // Email verification and sending code
    btnCheckEmail.addEventListener('click', function() {
        const email = document.getElementById('email').value;
        const emailCheckMessage = document.getElementById('email-check-message');

        console.log("Checking email:", email);

        if (!validateEmail(email)) {
            emailCheckMessage.textContent = "";
            emailCheckMessage.classList.add('d-none');
            alert("이메일 형식이 올바르지 않습니다.");
            return;
        }

        // Check email availability
        fetch(`/api/auth/check-email?email=${encodeURIComponent(email)}`)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    emailCheckMessage.textContent = "사용 가능한 이메일입니다.";
                    emailCheckMessage.style.color = "green";
                    emailCheckMessage.classList.remove('d-none');

                    // Send verification code if email is available
                    sendVerificationCode(email);
                } else {
                    emailCheckMessage.textContent = "이미 사용 중인 이메일입니다.";
                    emailCheckMessage.style.color = "red";
                    emailCheckMessage.classList.remove('d-none');
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });

    // Send verification code to the email
    function sendVerificationCode(email) {
        fetch(`/api/auth/send-verification-code?email=${encodeURIComponent(email)}`, {
            method: 'POST'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    alert("인증 코드가 발송되었습니다.");
                } else {
                    alert("인증 코드 발송에 실패했습니다: " + data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("인증 코드 발송 중 오류가 발생했습니다.");
            });
    }

    // Verify the code entered by the user
    btnVerifyCode.addEventListener('click', function() {
        const email = document.getElementById('email').value;
        const verificationCode = document.getElementById('verification-code').value;
        const verificationCodeMessage = document.getElementById('verification-code-message');

        console.log("Email:", email);
        console.log("Verification Code:", verificationCode);

        // Validate that the verification code is not empty
        if (!verificationCode) {
            verificationCodeMessage.textContent = "인증 코드를 입력해주세요.";
            verificationCodeMessage.style.color = "red";
            verificationCodeMessage.classList.remove('d-none');
            return;
        }

        // Create the request body
        const requestBody = {
            email: email,
            verificationCode: verificationCode
        };

        fetch(`/api/auth/verify-code`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody) // Convert the request body to JSON string
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(text => {
                        throw { status: response.status, message: response.message };
                    });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    verificationCodeMessage.textContent = "인증 코드가 확인되었습니다.";
                    verificationCodeMessage.style.color = "green";
                    verificationCodeMessage.classList.remove('d-none');
                } else {
                    verificationCodeMessage.textContent = "인증 코드가 일치하지 않습니다.";
                    verificationCodeMessage.style.color = "red";
                    verificationCodeMessage.classList.remove('d-none');
                }
            })
            .catch(error => {
                console.error("Error:", error);
                if (error.status === 401) {
                    verificationCodeMessage.textContent = "인증 코드가 만료되었습니다.";
                } else if (error.status === 400) {
                    verificationCodeMessage.textContent = "인증 코드가 일치하지 않습니다.";
                } else {
                    verificationCodeMessage.textContent = "서버 내부 오류입니다.";
                }
                verificationCodeMessage.style.color = "red";
                verificationCodeMessage.classList.remove('d-none');
            });
    });


    // Validate the form fields
    function validateForm() {
        const email = document.getElementById('email').value;
        const verificationCode = document.getElementById('verification-code').value;
        const password = document.getElementById('password').value;
        const passwordCheck = document.getElementById('password-check').value;
        const username = document.getElementById('username').value;
        const phone = document.getElementById('phone').value;
        const zonecode = document.getElementById('zonecode').value;
        const address = document.getElementById('address').value;
        const addressDetail = document.getElementById('address-detail').value;

        console.log("Validating form: ", { email, verificationCode, password, passwordCheck, username, phone, zonecode, address, addressDetail });

        // Email validation
        if (!validateEmail(email)) {
            alert("이메일 형식이 올바르지 않습니다.");
            return false;
        }

        // Verification code validation
        if (!verificationCode) {
            alert("인증 코드를 입력해주세요.");
            return false;
        }

        // Password validation
        if (password.length < 8 || password.length > 20) {
            alert("비밀번호는 8자 이상 20자 이하이어야 합니다.");
            return false;
        }
        if (!/(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])/.test(password)) {
            alert("비밀번호는 숫자, 소문자, 대문자를 각각 하나 이상 포함해야 합니다.");
            return false;
        }
        if (password !== passwordCheck) {
            alert("비밀번호가 일치하지 않습니다.");
            return false;
        }

        if (username.length < 3 || username.length > 10) {
            alert("아이디는 3자 이상 10자 이하이어야 합니다.");
            return false;
        }

        // Phone number validation
        if (!/^[0-9]{11}$/.test(phone)) {
            alert("핸드폰 번호는 11자리입니다.");
            return false;
        }

        // Address validation
        if (!zonecode || !address || !addressDetail) {
            alert("주소와 우편번호 및 상세주소는 필수 입력 사항입니다.");
            return false;
        }

        return true;
    }

    // Validate email format
    function validateEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    // Check username availability
    btnCheckId.addEventListener('click', function() {
        const username = document.getElementById('username').value;
        console.log("Checking username:", username);

        if (username.length < 3 || username.length > 10) {
            alert("사용자 이름은 3자 이상 10자 이하이어야 합니다.");
            return;
        }

        fetch(`/api/auth/username/${encodeURIComponent(username)}/exists`)
            .then(response => {
                console.log("Response status:", response.status);
                return response.json()})
            .then(data => {
                const idCheckMessage = document.getElementById('id-check-message');
                if (data.success) {
                    idCheckMessage.textContent = "사용 가능한 아이디입니다.";
                    idCheckMessage.style.color = "green";
                    idCheckMessage.classList.remove('d-none');
                } else {
                    idCheckMessage.textContent = "이미 사용 중인 아이디입니다.";
                    idCheckMessage.style.color = "red";
                    idCheckMessage.classList.remove('d-none');
                }
            })
            .catch(error => {
                console.error("Error:", error);
            });
    });
});
