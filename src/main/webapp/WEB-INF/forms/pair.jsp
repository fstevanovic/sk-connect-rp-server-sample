<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>SecureKey Sample RP</title>
    </head>
    <body>
        <h1>Get Pairing Code</h1>
        <p>Any userId will be accepted. To ensure uniqueness, we recommend using your email address.</p>
        <p>This will return pairing code thay you should enter in sample mobile application to pair the device with the user.</p>
        <form action="getPairingCode.json" method="get">
                <label for="userId">User ID:</label>
                <input type="text" id="userId" name="userId" />
                <button type="submit">Submit</button>
         </form>
    </body>
</html>
