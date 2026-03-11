let modalInstance;
let deleteModal;
let deleteEntryId;

let editModalInstance;
let verifyModalInstance;
let selectedEntryId = null;

let profileModal;

/* =========================
   AUTO LOGOUT (5 minutes)
========================= */

let inactivityTimer;

function resetTimer() {
    clearTimeout(inactivityTimer);
    inactivityTimer = setTimeout(logout, 300000);
}

document.onmousemove = resetTimer;
document.onkeypress = resetTimer;
document.onclick = resetTimer;

resetTimer();

/* =========================
   SECTION NAVIGATION
========================= */

function showSection(sectionName) {
    const mainContent = document.getElementById('mainContent');

    // Map section names to HTML files
    const sectionFiles = {
        overview: 'overview.html',
        vault: 'vault.html',
        generator: 'generator.html',
        audit: 'audit.html',
        profile: 'profile.html'
    };

    const file = sectionFiles[sectionName];
    if (!file) return;

    fetch(file)
        .then(res => {
            if (!res.ok) throw new Error('Failed to load section');
            return res.text();
        })
        .then(html => {
            mainContent.innerHTML = html;

            // Optional: call any JS initialization for the section
            if (sectionName === 'overview') loadDashboardSummary();
            if (sectionName === 'vault') loadPasswords();
            if (sectionName === 'audit') loadAuditReport();
        })
        .catch(err => {
            mainContent.innerHTML = `<p class="text-danger">Error loading section</p>`;
            console.error(err);
        });
}
/* =========================
   LOGOUT
========================= */

function logout() {

    fetch("/logout", {
        method: "POST",
        credentials: "same-origin"
    })
        .then(() => {
            sessionStorage.clear();
            window.location.href = "/login?logout=true";
        })
        .catch(() => {
            alert("Logout failed");
        });
}


/* =========================
   ADD PASSWORD MODAL
========================= */

function openAddModal() {

    modalInstance =
        new bootstrap.Modal(
            document.getElementById("addPasswordModal")
        );

    modalInstance.show();
}

function closeModal() {
    if (modalInstance) modalInstance.hide();
}


/* =========================
   FILTER VAULT FROM DASHBOARD
========================= */

function openVaultWithFilter(type) {
    // Save the filter type for vault page
    sessionStorage.setItem("vaultFilterType", type);

    // Navigate to vault page route
    window.location.href = "/vault"; // <-- calls your backend controller
}


/* =========================
   SAVE PASSWORD
========================= */

function savePassword() {

    let userId = sessionStorage.getItem("userId");

    if (!userId) {
        alert("User not logged in");
        return;
    }

    let payload = {
        accountName: document.getElementById("accountName").value,
        websiteUrl: document.getElementById("websiteUrl").value,
        usernameEmail: document.getElementById("usernameEmail").value,
        encryptedPassword: document.getElementById("encryptedPassword").value,
        category: document.getElementById("category").value,
        notes: document.getElementById("notes").value,
        isFavorite: document.getElementById("isFavorite").checked ? "Y" : "N"
    };

    fetch(`/api/passwords/${userId}/add`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
        .then(res => {

            if (res.ok) {
                alert("Saved successfully");
                closeModal();
                loadPasswords();
            } else {
                alert("Save failed");
            }

        })
        .catch(() => alert("Server error"));
}


/* =========================
   PASSWORD GENERATOR
========================= */

function toggleGeneratorOptions() {

    let box = document.getElementById("generatorOptions");

    box.style.display =
        box.style.display === "none" ? "block" : "none";
}


function generatePasswordCommon(length, upper, lower, numbers, special) {

    if (length < 8) {
        alert("Minimum password length is 8");
        return "";
    }

    let upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    let lowerChars = "abcdefghijklmnopqrstuvwxyz";
    let numberChars = "0123456789";
    let specialChars = "!@#$%^&*()_+=<>?";

    let allChars = "";
    let passwordArray = [];

    if (upper) {
        allChars += upperChars;
        passwordArray.push(
            upperChars[Math.floor(Math.random() * upperChars.length)]
        );
    }

    if (lower) {
        allChars += lowerChars;
        passwordArray.push(
            lowerChars[Math.floor(Math.random() * lowerChars.length)]
        );
    }

    if (numbers) {
        allChars += numberChars;
        passwordArray.push(
            numberChars[Math.floor(Math.random() * numberChars.length)]
        );
    }

    if (special) {
        allChars += specialChars;
        passwordArray.push(
            specialChars[Math.floor(Math.random() * specialChars.length)]
        );
    }

    while (passwordArray.length < length) {
        passwordArray.push(
            allChars[Math.floor(Math.random() * allChars.length)]
        );
    }

    passwordArray.sort(() => Math.random() - 0.5);

    return passwordArray.join('');
}


function generatePassword() {
    let length = document.getElementById("genLength").value;

    // Generate 3 different passwords
    let pass1 = generatePasswordCommon(length, upper.checked, lower.checked, numbers.checked, special.checked);
    let pass2 = generatePasswordCommon(length, upper.checked, lower.checked, numbers.checked, special.checked);
    let pass3 = generatePasswordCommon(length, upper.checked, lower.checked, numbers.checked, special.checked);

    // Build HTML with inline boxes
    let html = `
        <div class="d-flex justify-content-center gap-3 flex-wrap">
            <div class="input-group" style="max-width:250px;">
                <input type="text" id="generatedPassword1" class="form-control generated-box" readonly value="${pass1}">
                <button class="btn btn-outline-success" onclick="copyPassword('generatedPassword1')">
                    <i class="fa-solid fa-copy"></i>
                </button>
            </div>
            <div class="input-group" style="max-width:250px;">
                <input type="text" id="generatedPassword2" class="form-control generated-box" readonly value="${pass2}">
                <button class="btn btn-outline-success" onclick="copyPassword('generatedPassword2')">
                    <i class="fa-solid fa-copy"></i>
                </button>
            </div>
            <div class="input-group" style="max-width:250px;">
                <input type="text" id="generatedPassword3" class="form-control generated-box" readonly value="${pass3}">
                <button class="btn btn-outline-success" onclick="copyPassword('generatedPassword3')">
                    <i class="fa-solid fa-copy"></i>
                </button>
            </div>
        </div>
    `;

    document.getElementById("passwordContainer").innerHTML = html;
}


function generateForAccount() {

    let length = document.getElementById("genLengthAdd").value;

    let pass = generatePasswordCommon(
        length,
        addUpper.checked,
        addLower.checked,
        addNumbers.checked,
        addSpecial.checked
    );

    document.getElementById("encryptedPassword").value = pass;
    document.getElementById("generatorOptions").style.display = "none";
}

/* =========================
   PASSWORD STRENGTH INDICATOR
========================= */
function updateStrengthIndicator() {
    let count = 0;
    if (document.getElementById("upper")?.checked) count++;
    if (document.getElementById("lower")?.checked) count++;
    if (document.getElementById("numbers")?.checked) count++;
    if (document.getElementById("special")?.checked) count++;

    let bar = document.getElementById("strengthBar");
    if (!bar) return;

    switch (count) {
        case 1:
            bar.className = "progress-bar bg-danger";
            bar.style.width = "25%";
            bar.textContent = "Weak";
            break;
        case 2:
            bar.className = "progress-bar bg-warning text-dark";
            bar.style.width = "50%";
            bar.textContent = "Good";
            break;
        case 3:
            bar.className = "progress-bar bg-info";
            bar.style.width = "75%";
            bar.textContent = "Strong";
            break;
        case 4:
            bar.className = "progress-bar bg-success";
            bar.style.width = "100%";
            bar.textContent = "Very Strong";
            break;
        default:
            bar.className = "progress-bar bg-secondary";
            bar.style.width = "10%";
            bar.textContent = "None Selected";
    }
}
/* =========================
   LOAD PASSWORDS
========================= */

function loadPasswords() {

    let userId = sessionStorage.getItem("userId");
    if (!userId) return;

    // --- READ DASHBOARD FILTER FROM SESSION STORAGE ---
    let vaultFilter = sessionStorage.getItem("vaultFilterType") || "";
    sessionStorage.removeItem("vaultFilterType"); // clear after reading

    fetch(`/api/passwords/${userId}`)
        .then(res => res.json())
        .then(data => {
            // --- GET FILTER VALUES ---
            let search = document.getElementById("searchInput")?.value.toLowerCase() || "";
            let category = document.getElementById("categoryFilter")?.value || "";
            let strength = document.getElementById("strengthFilter")?.value || "";
            let favorite = document.getElementById("favoriteFilter")?.value || "";
            let sort = document.getElementById("sortOption")?.value || "";

            // --- APPLY DASHBOARD FILTER IF PRESENT ---
            if (vaultFilter) {
                if (vaultFilter === "ALL") {
                    strength = "";
                    favorite = "";
                } else if (["VERY_STRONG", "STRONG", "WEAK"].includes(vaultFilter)) {
                    strength = vaultFilter;
                    favorite = "";
                } else if (vaultFilter === "FAV") {
                    favorite = "FAV";
                    strength = "";
                }

                // Pre-select dropdowns
                if (document.getElementById("strengthFilter")) document.getElementById("strengthFilter").value = strength;
                if (document.getElementById("favoriteFilter")) document.getElementById("favoriteFilter").value = favorite;
            }

            // --- APPLY FILTERS ---
            let filteredData = data.filter(p => {
                const acc = (p.accountName || "").toLowerCase();
                const email = (p.usernameEmail || "").toLowerCase();

                if (search && !acc.includes(search) && !email.includes(search)) return false;

                if (category && p.category !== category) return false;
                if (strength && p.strength !== strength) return false;
                if (favorite === "FAV" && p.isFavorite !== "Y") return false;

                return true;
            });

            // --- APPLY SORTING ---
            if (sort === "AZ") filteredData.sort((a, b) => a.accountName.localeCompare(b.accountName));
            else if (sort === "ZA") filteredData.sort((a, b) => b.accountName.localeCompare(a.accountName));
            else if (sort === "LATEST") filteredData.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
            else if (sort === "OLDEST") filteredData.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt));
            else if (sort === "MODIFIED") filteredData.sort((a, b) => new Date(b.modifiedAt) - new Date(a.modifiedAt));

            // --- BUILD TABLE ---
            let html = "";
            filteredData.forEach(p => {
                html += `
                <tr>
                    <td>${p.accountName || ""}</td>
                    <td>${p.websiteUrl || ""}</td>
                    <td>${p.usernameEmail || ""}</td>
                    <td>${p.category || ""}</td>
                    <td>${p.isFavorite === "Y" ? "★" : ""}</td>
                    <td><button class="btn btn-info btn-sm" onclick="verifyAndView(${p.entryId})">View</button></td>
                    <td><button class="btn btn-warning btn-sm" onclick="openEditModal(${p.entryId})">Edit</button></td>
                    <td><button class="btn btn-danger btn-sm" onclick="deletePassword(${p.entryId})">Delete</button></td>
                </tr>`;
            });

            if (document.getElementById("vaultTable")) {
                document.getElementById("vaultTable").innerHTML = html;
            }

            // --- UPDATE DASHBOARD CARDS ---
            let totalElem = document.getElementById("totalPasswords");
            if (totalElem) totalElem.innerText = data.length;

            let veryStrongElem = document.getElementById("veryStrongPasswords");
            if (veryStrongElem) veryStrongElem.innerText = data.filter(p => p.strength === "VERY_STRONG").length;

            let strongElem = document.getElementById("strongPasswords");
            if (strongElem) strongElem.innerText = data.filter(p => p.strength === "STRONG").length;

            let weakElem = document.getElementById("weakPasswords");
            if (weakElem) weakElem.innerText = data.filter(p => p.strength === "WEAK").length;

            let favElem = document.getElementById("favoriteCount");
            if (favElem) favElem.innerText = data.filter(p => p.isFavorite === "Y").length;
        })
        .catch(err => {
            console.error("Failed to load passwords:", err);
            if (document.getElementById("vaultTable")) {
                document.getElementById("vaultTable").innerHTML = "<tr><td colspan='8' class='text-danger'>Failed to load passwords</td></tr>";
            }
        });
}



/* =========================
   DELETE PASSWORD
========================= */

function deletePassword(entryId) {

    deleteEntryId = entryId;

    deleteModal =
        new bootstrap.Modal(
            document.getElementById("deleteVerifyModal")
        );

    document.getElementById("deleteMasterPassword").value = "";
    document.getElementById("deleteError").innerText = "";

    deleteModal.show();
}


function closeDeleteModal() {
    if (deleteModal) deleteModal.hide();
}


function confirmDelete() {

    let userId = sessionStorage.getItem("userId");

    let masterPassword =
        document.getElementById("deleteMasterPassword").value;

    fetch(`/api/passwords/${userId}/delete/${deleteEntryId}`, {

        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ masterPassword })

    })
        .then(res => {

            if (res.ok) {

                alert("Password Deleted Successfully");

                closeDeleteModal();

                loadPasswords();

            } else {

                document.getElementById("deleteError").innerText =
                    "Wrong Master Password";

            }

        });
}


/* =========================
   VIEW PASSWORD
========================= */

function verifyAndView(entryId) {

    selectedEntryId = entryId;

    verifyModalInstance =
        new bootstrap.Modal(
            document.getElementById("verifyMasterModal")
        );

    verifyModalInstance.show();
}


function closeVerifyModal() {
    if (verifyModalInstance) verifyModalInstance.hide();
}


function confirmViewPassword() {

    let userId = sessionStorage.getItem("userId");

    let masterPassword =
        document.getElementById("masterPasswordInput").value;

    fetch(`/api/passwords/${userId}/verify/${selectedEntryId}`, {
        method: "POST",
        credentials: "same-origin",  // <-- ADD THIS LINE
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ masterPassword })
    })
        .then(res => {
            if (!res.ok) throw new Error();
            return res.text();
        })
        .then(password => {
            closeVerifyModal();
            alert("Account Password : " + password);
        })
        .catch(() => {
            document.getElementById("verifyError").innerText = "Wrong Master Password";
        });
}

/* =========================
   Load Audit
========================= */

function loadAuditReport() {
    let userId = sessionStorage.getItem("userId");
    if (!userId) {
        alert("User not logged in");
        return;
    }

    fetch(`/api/audit/${userId}?daysThreshold=90`)
        .then(res => res.json())
        .then(data => {
            let html = "<h5>Weak Passwords</h5>";
            if (data.weakPasswords.length === 0) html += "<p>None</p>";
            else data.weakPasswords.forEach(p => {
                html += `<p class="strength-weak">${p.accountName} (${p.usernameEmail})</p>`;
            });

            html += "<h5>Reused Passwords</h5>";
            if (data.reusedPasswords.length === 0) html += "<p>None</p>";
            else data.reusedPasswords.forEach(p => {
                html += `<p class="strength-medium">${p.accountName} (${p.usernameEmail})</p>`;
            });

            html += "<h5>Old Passwords (90+ days)</h5>";
            if (data.oldPasswords.length === 0) html += "<p>None</p>";
            else data.oldPasswords.forEach(p => {
                html += `<p class="strength-strong">${p.accountName} (${p.usernameEmail})</p>`;
            });

            document.getElementById("auditReport").innerHTML = html;
        })
        .catch(err => {
            console.error("Audit fetch error:", err);
            document.getElementById("auditReport").innerHTML =
                "<p class='text-danger'>Failed to load audit report</p>";
        });
}
/* =========================
   PROFILE UPDATE
========================= */

function updateProfile() {

    profileModal =
        new bootstrap.Modal(
            document.getElementById("profileVerifyModal")
        );

    document.getElementById("profileMasterPassword").value = "";
    document.getElementById("profileError").innerText = "";

    profileModal.show();
}


function closeProfileVerify() {
    if (profileModal) profileModal.hide();
}


function confirmProfileUpdate() {

    let userId = sessionStorage.getItem("userId");

    let payload = {

        fullName: document.getElementById("profileName").value,

        email: document.getElementById("profileEmail").value,

        phoneNumber: document.getElementById("profilePhone").value,

        newPassword: document.getElementById("newMasterPassword").value,

        masterPassword:
            document.getElementById("profileMasterPassword").value
    };

    fetch(`/api/profile/${userId}/update`, {

        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)

    })
        .then(res => {

            if (res.ok) {

                alert("Profile Updated Successfully");

                closeProfileVerify();

            } else {

                document.getElementById("profileError").innerText =
                    "Wrong Master Password";

            }

        });
}


/* =========================
   DASHBOARD SUMMARY
========================= */

function loadDashboardSummary() {
    let userId = sessionStorage.getItem("userId");
    if (!userId) return;

    return fetch(`/api/dashboard/${userId}`)
        .then(res => res.json())
        .then(data => {
            // --- Update dashboard cards ---
            document.getElementById("totalPasswords").innerText = data.total || 0;
            document.getElementById("veryStrongPasswords").innerText = data.veryStrong || 0;
            document.getElementById("strongPasswords").innerText = data.strong || 0;
            document.getElementById("weakPasswords").innerText = data.weak || 0;
            document.getElementById("favoriteCount").innerText = (data.favorites?.length) || 0;

            // --- NEW: Make Weak Passwords card blink attractively if weak passwords exist ---
            let weakCard = document.querySelector("#overview .col-md-3:nth-child(4) .card-box");
            if (data.weak > 0) {
                weakCard.classList.add("card-blink");
            } else {
                weakCard.classList.remove("card-blink");
            }
        })
        .catch(err => {
            console.error("Failed to load dashboard summary:", err);
            document.getElementById("totalPasswords").innerText = 0;
            document.getElementById("veryStrongPasswords").innerText = 0;
            document.getElementById("strongPasswords").innerText = 0;
            document.getElementById("weakPasswords").innerText = 0;
            document.getElementById("favoriteCount").innerText = 0;
        });
}
/* =========================
   RESET FILTERS
========================= */

function resetFilters() {

    document.getElementById("searchInput").value = "";
    document.getElementById("categoryFilter").value = "";
    document.getElementById("sortOption").value = "";
    document.getElementById("strengthFilter").value = "";
    document.getElementById("favoriteFilter").value = "";

    loadPasswords();

}


/* =========================
   OPEN EDIT MODAL
========================= */

function openEditModal(entryId) {
    const userId = sessionStorage.getItem("userId");
    if (!userId) { alert("User not logged in"); return; }

    fetch(`/api/passwords/${userId}/get/${entryId}`)
        .then(res => {
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return res.json();
        })
        .then(p => {
            document.getElementById("editEntryId").value = p.entryId;
            document.getElementById("editAccountName").value = p.accountName || "";
            document.getElementById("editWebsiteUrl").value = p.websiteUrl || "";
            document.getElementById("editUsernameEmail").value = p.usernameEmail || "";
            document.getElementById("editCategory").value = p.category || "";
            document.getElementById("editFavorite").checked = (p.isFavorite === "Y");

            // Password field is empty
            document.getElementById("editPassword").value = "";
            document.getElementById("originalPassword").value = "";

            // Open modal
            editModalInstance = new bootstrap.Modal(document.getElementById("editPasswordModal"));
            editModalInstance.show();
        })
        .catch(err => {
            console.error("Edit modal fetch error:", err);
            alert("Failed to load password data");
        });
}


let pendingEditEntryId = null;
let pendingEditEntryData = null;


function updatePassword() {
    const userId = sessionStorage.getItem("userId");
    if (!userId) { alert("User not logged in"); return; }

    const entryId = document.getElementById("editEntryId").value;
    const newPassword = document.getElementById("editPassword").value;

    // First, fetch the current encrypted password from hidden field
    const originalEncryptedPassword = document.getElementById("originalPassword").value;

    const updatedEntry = {
        accountName: document.getElementById("editAccountName").value,
        websiteUrl: document.getElementById("editWebsiteUrl").value,
        usernameEmail: document.getElementById("editUsernameEmail").value,
        category: document.getElementById("editCategory").value,
        isFavorite: document.getElementById("editFavorite").checked ? "Y" : "N",
        encryptedPassword: originalEncryptedPassword // default: keep old
    };

    if (newPassword && newPassword.length > 0) {
        // User wants to change password → verify master password first
        pendingEditEntryId = entryId;
        pendingEditEntryData = { ...updatedEntry, encryptedPassword: newPassword };

        const editModalInstance = bootstrap.Modal.getInstance(document.getElementById("editPasswordModal"));
        if (editModalInstance) editModalInstance.hide();

        const verifyModal = new bootstrap.Modal(document.getElementById("editVerifyMasterModal"));
        document.getElementById("editMasterPasswordInput").value = "";
        document.getElementById("editVerifyError").innerText = "";
        verifyModal.show();
    } else {
        // Password not changed → update other fields directly, send original encrypted password
        fetch(`/api/passwords/${userId}/update/${entryId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedEntry)
        })
        .then(res => {
            if (!res.ok) throw new Error("Update failed");
            alert("Updated successfully");
            closeEditModal();
            loadPasswords();
        })
        .catch(() => alert("Update failed"));
    }
}

function confirmEditMasterPassword() {
    const userId = sessionStorage.getItem("userId");
    const entryId = pendingEditEntryId;
    const updatedEntry = pendingEditEntryData;
    const masterPassword = document.getElementById("editMasterPasswordInput").value;

    fetch(`/api/passwords/${userId}/verify/${entryId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ masterPassword })
    })
    .then(res => {
        if (!res.ok) throw new Error("Wrong master password");
        return res.text();
    })
    .then(() => {
        // Verified → update password
        fetch(`/api/passwords/${userId}/update/${entryId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(updatedEntry)
        })
        .then(res => {
            if (!res.ok) throw new Error("Update failed");
            alert("Updated successfully");

            // Close modals
            closeEditModal();
            const modal = bootstrap.Modal.getInstance(document.getElementById("editVerifyMasterModal"));
            modal.hide();

            loadPasswords();
        })
        .catch(() => alert("Update failed"));
    })
    .catch(() => {
        document.getElementById("editVerifyError").innerText = "Wrong Master Password";
    });
}
/* =========================
   CLOSE EDIT MODAL
========================= */

function closeEditModal() {

    if (editModalInstance)
        editModalInstance.hide();

}


/* =========================
   COPY GENERATED PASSWORD
========================= */
/* =========================
   COPY GENERATED PASSWORD
========================= */

function copyPassword(inputId) {
    let pass = document.getElementById(inputId).value;
    if (!pass) {
        alert("Generate password first");
        return;
    }
    navigator.clipboard.writeText(pass)
        .then(() => { alert("Password copied to clipboard"); })
        .catch(() => { alert("Copy failed"); });
}

// ================= UPDATE SECURITY ANSWERS =================

// ================= UPDATE SECURITY QUESTIONS =================

function loadSecurityQuestions() {
    const userId = sessionStorage.getItem("userId");

    fetch(`/api/auth/security-questions/current`)
        .then(res => res.json())
        .then(userAnswers => {
            fetch(`/api/auth/all-security-questions`)
                .then(res => res.json())
                .then(allQuestions => {
                    ["q1", "q2", "q3"].forEach((qId, idx) => {
                        const select = document.getElementById(qId);
                        select.innerHTML = "";

                        allQuestions.forEach(q => {
                            const opt = document.createElement("option");
                            opt.value = q.questionId;
                            opt.text = q.questionText;
                            select.appendChild(opt);
                        });

                        // Preselect current saved question
                        if (userAnswers[idx]) {
                            select.value = userAnswers[idx].question.questionId;
                            document.getElementById("a" + (idx + 1)).value = ""; // keep answer blank
                        }
                    });
                });
        })
        .catch(err => console.error("Failed to load security questions", err));
}

function saveSecurityQuestions() {
    window.pendingSecurityQuestions = [
        { questionId: parseInt(document.getElementById("q1").value), answer: document.getElementById("a1").value },
        { questionId: parseInt(document.getElementById("q2").value), answer: document.getElementById("a2").value },
        { questionId: parseInt(document.getElementById("q3").value), answer: document.getElementById("a3").value }
    ];

    const modal = new bootstrap.Modal(document.getElementById("profileVerifyModal"));
    modal.show();
}

function confirmSecurityQuestionUpdate() {
    // Get password from input
    const password = document.getElementById("profileMasterPassword").value;

    if (!password) {
        document.getElementById("profileError").innerText = "Enter master password";
        return;
    }

    const userId = sessionStorage.getItem("userId"); // or however you store userId

    fetch('/api/auth/verify-master-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: userId, masterPassword: password })
    })
    .then(res => res.json())
    .then(data => {
        if (data.valid) {
            // Close modal
            const modal = bootstrap.Modal.getInstance(document.getElementById("verifyMasterForSecurityModal"));
            if (modal) modal.hide();

            // Now update security questions
            updateSecurityQuestions();
        } else {
            document.getElementById("profileError").innerText = "Wrong master password";
        }
    })
    .catch(err => {
        console.error(err);
        document.getElementById("profileError").innerText = "Server error";
    });
}


let pendingSecurityQuestions = [];

// Load only questions for profile view
function loadSecurityQuestions() {
    const userId = sessionStorage.getItem("userId");

    fetch(`/api/auth/all-security-questions`)
        .then(res => res.json())
        .then(allQuestions => {

            fetch(`/api/auth/security-questions/${userId}`)
                .then(res => res.json())
                .then(userAnswers => {

                    const list = document.getElementById("userQuestionsList");
                    list.innerHTML = "";

                    userAnswers.forEach(ans => {
                        const li = document.createElement("li");
                        li.className = "list-group-item";
                        li.textContent = ans.question.questionText; // show only question
                        list.appendChild(li);
                    });
                });
        });
}

// Open edit modal
function openEditSecurityModal() {
    const userId = sessionStorage.getItem("userId");

    Promise.all([
        fetch(`/api/auth/all-security-questions`).then(res => res.json()),
        fetch(`/api/auth/security-questions/${userId}`).then(res => res.json())
    ]).then(([allQuestions, userAnswers]) => {

        const container = document.getElementById("editQuestionsContainer");
        container.innerHTML = "";

        userAnswers.forEach((ans, idx) => {
            const div = document.createElement("div");
            div.className = "mb-3";

            // Question select
            const select = document.createElement("select");
            select.className = "form-select mb-1";
            select.id = `q${idx+1}`;

            allQuestions.forEach(q => {
                const option = document.createElement("option");
                option.value = q.questionId;
                option.textContent = q.questionText;
                if (q.questionId === ans.question.questionId) option.selected = true;
                select.appendChild(option);
            });

            // Answer input
            const input = document.createElement("input");
            input.type = "text";
            input.id = `a${idx+1}`;
            input.className = "form-control";
            input.placeholder = "Enter answer";

            div.appendChild(select);
            div.appendChild(input);

            container.appendChild(div);
        });

        const modal = new bootstrap.Modal(document.getElementById("editSecurityModal"));
        modal.show();
    });
}

// Prepare update: collect answers and open master password modal
function prepareSecurityUpdate() {
    pendingSecurityQuestions = [
        { questionId: parseInt(document.getElementById("q1").value), answer: document.getElementById("a1").value },
        { questionId: parseInt(document.getElementById("q2").value), answer: document.getElementById("a2").value },
        { questionId: parseInt(document.getElementById("q3").value), answer: document.getElementById("a3").value }
    ];

    // Close edit modal
    const editModal = bootstrap.Modal.getInstance(document.getElementById("editSecurityModal"));
    editModal.hide();

    // Open master password verify modal
    const verifyModal = new bootstrap.Modal(document.getElementById("verifyMasterForSecurityModal"));
    document.getElementById("profileMasterPassword").value = "";
    document.getElementById("profileError").innerText = "";
    verifyModal.show();
}

// Confirm master password and save
function confirmSecurityQuestionUpdate() {
    const userId = sessionStorage.getItem("userId");
    const masterPassword = document.getElementById("profileMasterPassword").value;

	fetch('/api/auth/verify-master-password', {
	    method: 'POST',
	    headers: {'Content-Type': 'application/json'},
	    body: JSON.stringify({ userId, masterPassword: password })
	})
    .then(res => {
        if (!res.ok) throw new Error("Wrong master password");
        return res.json();
    })
    .then(() => {
        // Save security answers
        fetch("/api/auth/save-security-answers", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId, answers: pendingSecurityQuestions })
        })
        .then(res => res.json())
        .then(data => {
            alert("Security questions updated successfully");
            loadSecurityQuestions(); // reload view-only list
            const modal = bootstrap.Modal.getInstance(document.getElementById("verifyMasterForSecurityModal"));
            modal.hide();
        });
    })
    .catch(err => {
        document.getElementById("profileError").innerText = "Wrong Master Password";
    });
}

// On page load

// Keep your original function as-is
function loadSecurityQuestions() {
    // ...existing logic...
}

// New function to load only current user's questions
function loadUserSecurityQuestions() {
    const userId = sessionStorage.getItem("userId");
    if (!userId) return;

    fetch(`/api/auth/security-questions/${userId}`)
        .then(res => res.json())
        .then(userAnswers => {
            const list = document.getElementById("userQuestionsList");
            if (list) {
                list.innerHTML = "";
                userAnswers.forEach(ans => {
                    const li = document.createElement("li");
                    li.className = "list-group-item";
                    li.textContent = ans.question.questionText;
                    list.appendChild(li);
                });
            }
        })
        .catch(err => console.error("Failed to load user security questions:", err));
}

// On profile page load, call loadUserSecurityQuestions instead of default
function loadUserSecurityQuestions() {
    const userId = sessionStorage.getItem("userId");
    if (!userId) {
        console.error("No userId found in sessionStorage");
        return;
    }

    fetch(`/api/auth/security-questions/${userId}`)
        .then(res => res.json())
        .then(userAnswers => {
            console.log("UserAnswers fetched:", userAnswers.length);
            const list = document.getElementById("userQuestionsList");
            if (list) {
                list.innerHTML = "";
                userAnswers.forEach(ans => {
                    const li = document.createElement("li");
                    li.className = "list-group-item";
                    li.textContent = ans.question.questionText;
                    list.appendChild(li);
                });
            }
        })
        .catch(err => console.error("Failed to load user security questions:", err));
}


/* =========================
   EXPORT VAULT
========================= */
function exportVault() {
    let userId = sessionStorage.getItem("userId");
    if (!userId) {
        alert("User not logged in");
        return;
    }

    fetch(`/api/passwords/${userId}/export`)
        .then(res => {
            if (!res.ok) throw new Error("Export failed");
            return res.blob();
        })
        .then(blob => {
            let url = window.URL.createObjectURL(blob);
            let a = document.createElement("a");
            a.href = url;
            a.download = "vault-backup.json"; // filename for download
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        })
        .catch(err => {
            console.error("Export error:", err);
            alert("Export failed");
        });
}


//import function//
function importVault() {
    let userId = sessionStorage.getItem("userId");
    if (!userId) {
        alert("User not logged in");
        return;
    }

    let fileInput = document.getElementById("importFile");
    if (!fileInput.files.length) {
        alert("Please choose a file");
        return;
    }

    let formData = new FormData();
    formData.append("file", fileInput.files[0]);

    fetch(`/api/passwords/${userId}/import`, {
        method: "POST",
        body: formData
    })
    .then(res => {
        if (!res.ok) throw new Error("Import failed");
        return res.text();
    })
    .then(msg => {
        alert(msg);
        loadPasswords(); // reload vault table after import
    })
    .catch(err => {
        console.error("Import error:", err);
        alert("Import failed");
    });
}



/* =========================
   PAGE LOAD
========================= */
window.addEventListener("load", function() {
    // Security questions list
	if(document.getElementById("editQuestionsContainer")) {
	    loadSecurityQuestions();
	}

	// For profile.html or vault.html (read-only user questions list)
	if(document.getElementById("userQuestionsList")) {
	    loadUserSecurityQuestions();
	}
    // Vault search input
    const searchInput = document.getElementById("searchInput");
    if (searchInput) searchInput.addEventListener("input", loadPasswords);

    // Load vault passwords table
    if (document.getElementById("vaultTable")) loadPasswords();

    // Dashboard summary
    if (document.getElementById("totalPasswords")) loadDashboardSummary();

    // Audit report
    if (document.getElementById("auditReport")) loadAuditReport();
});