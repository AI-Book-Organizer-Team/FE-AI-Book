import {onCall} from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";
import * as sgMail from "@sendgrid/mail";

// Firebase Admin 초기화
admin.initializeApp();

// SendGrid API 키 설정
const SENDGRID_API_KEY = process.env.SENDGRID_API_KEY;
const FROM_EMAIL = process.env.FROM_EMAIL || "noreply@fe-ai-book.com";

if (!SENDGRID_API_KEY) {
  logger.error("SENDGRID_API_KEY 환경변수가 설정되지 않았습니다.");
} else {
  sgMail.setApiKey(SENDGRID_API_KEY);
}

/**
 * 이메일 인증번호 전송 함수
 */
export const sendEmailVerification = onCall(async (request) => {
  try {
    const {email} = request.data;
    
    // 입력값 검증
    if (!email || typeof email !== "string") {
      throw new Error("이메일 주소가 필요합니다.");
    }
    
    // 6자리 랜덤 인증번호 생성
    const verificationCode = Math.floor(100000 + Math.random() * 900000).toString();
    
    // 만료 시간 설정 (5분)
    const expiresAt = new Date(Date.now() + 5 * 60 * 1000);
    
    // Firestore에 인증번호 저장
    await admin.firestore().collection("email_verifications").doc(email).set({
      code: verificationCode,
      expiresAt: expiresAt,
      createdAt: new Date(),
      verified: false
    });
    
    // 이메일 전송
    const msg = {
      to: email,
      from: FROM_EMAIL, // 환경변수에서 가져온 발신자 이메일
      subject: "[FE-AI-Book] 이메일 인증번호",
      text: `인증번호: ${verificationCode}`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2 style="color: #333;">이메일 인증번호</h2>
          <p>안녕하세요! FE-AI-Book 회원가입을 위한 인증번호입니다.</p>
          <div style="background: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0;">
            <h1 style="color: #007bff; font-size: 36px; margin: 0;">${verificationCode}</h1>
          </div>
          <p style="color: #666;">이 인증번호는 5분 후에 만료됩니다.</p>
          <p style="color: #999; font-size: 12px;">본 메일은 발신전용입니다.</p>
        </div>
      `
    };
    
    await sgMail.send(msg);
    
    logger.info(`인증번호 전송 성공: ${email}`);
    return { success: true, message: "인증번호가 전송되었습니다." };
    
  } catch (error) {
    logger.error("인증번호 전송 실패:", error);
    throw new Error("인증번호 전송에 실패했습니다.");
  }
});

/**
 * 이메일 인증번호 확인 함수
 */
export const verifyEmailCode = onCall(async (request) => {
  try {
    const {email, code} = request.data;
    
    // 입력값 검증
    if (!email || !code) {
      throw new Error("이메일과 인증번호가 필요합니다.");
    }
    
    // Firestore에서 인증번호 조회
    const doc = await admin.firestore().collection("email_verifications").doc(email).get();
    
    if (!doc.exists) {
      throw new Error("인증번호를 찾을 수 없습니다. 다시 발송해주세요.");
    }
    
    const data = doc.data()!;
    
    // 이미 인증된 경우
    if (data.verified) {
      return { success: true, message: "이미 인증된 이메일입니다." };
    }
    
    // 만료 시간 확인
    if (new Date() > data.expiresAt.toDate()) {
      // 만료된 인증번호 삭제
      await admin.firestore().collection("email_verifications").doc(email).delete();
      throw new Error("인증번호가 만료되었습니다. 다시 발송해주세요.");
    }
    
    // 인증번호 확인
    if (data.code !== code) {
      throw new Error("올바르지 않은 인증번호입니다.");
    }
    
    // 인증 완료 처리
    await admin.firestore().collection("email_verifications").doc(email).update({
      verified: true,
      verifiedAt: new Date()
    });
    
    logger.info(`이메일 인증 성공: ${email}`);
    return { success: true, message: "이메일 인증이 완료되었습니다." };
    
  } catch (error) {
    logger.error("인증번호 확인 실패:", error);
    throw new Error(error instanceof Error ? error.message : "인증번호 확인에 실패했습니다.");
  }
});

/**
 * 이메일 인증 상태 확인 함수
 */
export const checkEmailVerification = onCall(async (request) => {
  try {
    const {email} = request.data;
    
    if (!email) {
      throw new Error("이메일 주소가 필요합니다.");
    }
    
    const doc = await admin.firestore().collection("email_verifications").doc(email).get();
    
    if (!doc.exists) {
      return { verified: false, message: "인증 정보가 없습니다." };
    }
    
    const data = doc.data()!;
    return { verified: data.verified || false };
    
  } catch (error) {
    logger.error("인증 상태 확인 실패:", error);
    throw new Error("인증 상태 확인에 실패했습니다.");
  }
});
