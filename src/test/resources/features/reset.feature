Feature: Reset
    Scenario: ResetPasswordFeatureTest
        Given [RESET] User sends reset password "POST" request to "http://localhost:8084/reset_password"
        Then [RESET] The response on "/reset_password" code should be 200
        And [RESET] The response on "/reset_password" should match JSON:
        """
        {
            "message": "Password reset code sent to email"
        }
        """
        Given [RESET] User sends reset password verification "POST" request to "http://localhost:8084/verify_reset_password"
        Then [RESET] The response on "/verify_reset_password" code should be 200
        And [RESET] The response on "/verify_reset_password" should match JSON:
        """
        {
            "message": "Password changed successfully!"
        }
        """
