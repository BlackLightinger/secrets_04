Feature: Login
    Scenario: LoginFeatureTest
        Given [LOGIN] User sends login "POST" request to "http://localhost:8084/login"
        Then [LOGIN] The response on "/login" code should be 200
        And [LOGIN] The response on "/login" should match JSON:
        """
        {
            "message": "Verification code sent to email"
        }
        """
        Given [LOGIN] User sends login verification "POST" request to "http://localhost:8084/verify"
        Then [LOGIN] The response on "/verify" code should be 200
        And [LOGIN] The response on "/verify" should match JSON:
        """
        {
            "message": "Auth success!"
        }
        """
