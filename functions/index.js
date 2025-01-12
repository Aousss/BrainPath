const { onCall } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");

admin.initializeApp();

// Configure Nodemailer with Outlook
const transporter = nodemailer.createTransport({
  service: "hotmail", // Use "hotmail" for Outlook
  auth: {
    user: "brainpathum@outlook.com",
    pass: "BrainPath100%",
  },
});

// Cloud Function to send a verification code
exports.sendVerificationCode = onCall({ region: "asia-southeast1" }, async (data, context) => {
  const email = data.email;
  const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();

  try {
    // Store the verification code in Firestore
    const db = admin.firestore();
    await db.collection("password_resets").doc(email).set({
      email: email,
      verificationCode: verificationCode,
      timestamp: Date.now(),
    });

    // Send the email
    const mailOptions = {
      from: "brainpathum@outlook.com",
      to: email,
      subject: "Your Password Reset Code",
      text: `Your verification code is: ${verificationCode}`,
    };

    await transporter.sendMail(mailOptions);
    logger.info(`Verification code sent to ${email}`);
    return { success: true };
  } catch (error) {
    logger.error("Error sending verification code:", error);
    throw new Error("Failed to send verification code");
  }
});
