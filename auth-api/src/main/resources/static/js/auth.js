document.addEventListener("DOMContentLoaded", function () {
  const emailInput = document.getElementById("email-input");
  const errorMessage = document.querySelector(".invalid-email-error-message");
  const emailLabel = document.querySelector(".email-label");
  const continueButton = document.getElementById("continue");
  const userEmail = sessionStorage.getItem("email");

  // Function to validate email
  function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  // Pre-fill the email input with the stored email (if it exists)
  if (userEmail) {
    emailInput.value = userEmail; // Set the email input value
    emailLabel.classList.add("filled"); // Add the "filled" class to the label
    continueButton.disabled = false; // Enable the continue button
  }

  // Event listener for input changes
  emailInput.addEventListener("input", function () {
    const email = emailInput.value.trim();

    // Add/remove the "filled" class based on whether the input has content
    if (email) {
      emailLabel.classList.add("filled");
      continueButton.disabled = false;
    } else {
      emailLabel.classList.remove("filled");
      continueButton.disabled = true;
    }

    // Hide the error message if it's visible and the input is being edited
    if (
      errorMessage.classList.contains("visible") ||
      emailInput.classList.contains("error")
    ) {
      errorMessage.classList.remove("visible");
      emailInput.classList.remove("error");
    }
  });

  // Event listener for form submission
  document.getElementById("auth-form").addEventListener("submit", function (e) {
    e.preventDefault(); // Prevent form submission
    const email = emailInput.value.trim();

    // Show/hide error message based on email validity
    if (!validateEmail(email)) {
      errorMessage.classList.add("visible"); // Show error message
      emailInput.classList.add("error"); // Add red border to input
    } else {
      errorMessage.classList.remove("visible"); // Hide error message
      emailInput.classList.remove("error"); // Remove red border from input
      sessionStorage.setItem("email", email); // Save email to session
      window.location.href = "signupPwd.html";
    }

    // Always allow form submission
    // Do not call e.preventDefault()
  });
});
