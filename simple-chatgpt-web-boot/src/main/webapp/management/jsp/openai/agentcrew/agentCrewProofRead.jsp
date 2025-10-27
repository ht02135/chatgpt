<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Agent Crew ProofRead</title>
    <script src="${pageContext.request.contextPath}/management/js/jquery-3.6.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/management/js/constants.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        textarea { width: 100%; height: 150px; }
        #result { margin-top: 20px; white-space: pre-wrap; border: 1px solid #ccc; padding: 10px; }
    </style>
</head>
<body>
    <h1>Agent Crew ProofRead</h1>

    <label for="inputText">Enter text for proof-reading:</label><br>
    <textarea id="inputText"></textarea><br><br>
    <button id="submitBtn">Submit</button>

    <div id="result"></div>

    <script>
        $(document).ready(function() {
            console.debug("agentCrewProofRead.jsp ready");

            $('#submitBtn').click(function() {
                const input = $('#inputText').val();
                console.debug("Submit clicked, input=", input);

                $('#result').text("Processing...");

                $.ajax({
                    url: API_OPENAI_AGENT_CREW + '/proofread',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(input),
                    success: function(response) {
                        console.debug("AJAX success response=", response);

                        if(response.status === "SUCCESS") {
                            $('#result').text(response.data);
                        } else {
                            $('#result').text("Error: " + response.message);
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error("AJAX error:", status, error);
                        $('#result').text("AJAX request failed: " + error);
                    }
                });
            });
        });
    </script>
</body>
</html>
