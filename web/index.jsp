<%--
  Created by IntelliJ IDEA.
  User: Utilisateur
  Date: 2015-11-24
  Time: 10:28 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>HockeyLive</title>
    <script src="scripts/jquery-2.1.4.min.js"></script>
    <script>
        var eventSource = new EventSource("NotificationServlet");

        eventSource.onmessage = function (event) {
            var notificationSection = $("#notification-section");
            var obj = jQuery.parseJSON(event.data);
            notificationSection.prepend("<p>" + event.data + "</p>");
            if(notificationSection.children().length > 10)
                notificationSection.children().last().remove();
        };

        eventSource.addEventListener('up_vote', function (event) {
            $("#notification-section").prepend("<p>" + event.data + "</p>");
        }, false);

        var gameSection = $("#game-section");

        var post = function (url, data, callback) {
            $.post(url, data).done(callback);
        }

        var timeoutID = null;
        var currentGameID = null;

        var refresh = function () {
            getGame(currentGameID);
        }

        var bet = function() {
            var betOnHost = $("#bet-on-host:checked").length > 0;
            var betOnVisitor = $("#bet-on-visitor:checked").length > 0;
            var amount = $("#bet-amount").value();

            if(amount != "" && (betOnHost || betOnVisitor)) {
                post("bet", {betOnHost: betOnHost, amount: amount}, function(data) {
                    alert(data);
                });
            }
        }

        $("#btn-bet").onclick(bet);

        var getGame = function (gameID) {
            clearTimeout(timeoutID);

            post("getGame", {gameID: gameID}, function (data) {
                gameSection.removeClass("hidden");

                var obj = jQuery.parseJSON(data);
                $("#game-section-title").innerHTML = data.title;
                $("#host-name").innerHTML = data.host;
                $("#host-goals").innerHTML = data.hostGoals;
                $("#host-penalties").innerHTML = data.hostPenalties;
                $("#visitor-name").innerHTML = data.visitor;
                $("#visitor-goals").innerHTML = data.visitorGoals;
                $("#visitor-penalties").innerHTML = data.visitorPenalties;

                timeoutID = setTimeout(refresh, 120000);
            });
        }

        post("getGames", {}, function (data) {
            var obj = jQuery.parseJSON(data);

            $.each(data, function (i, obj) {
                var btnGame = $("<button>");
                btnGame.innerHTML = obj.toString();
                btnGame.onclick(function () {
                    getGame(obj.id);
                });
                $("#games-section").append(btnGame);
            });
        });
    </script>
</head>
<body>
<table style="width: 100%; margin: 0;">
    <tr>
        <td style="width: 20%; vertical-align: top;">
            <h3>Games</h3>
            <div id="games-section"/>
        </td>
        <td style="width: 60%; vertical-align: top;">
            <div id="game-section" class="hidden">
                <h3 id="game-section-title">Test</h3>

                <table style="width: 100%">
                    <tr>
                        <td>
                            <h4 id="host-name">Test</h4>
                            <p>Goals: <span id="host-goals">0</span></p>
                            <p>Penalties: <span id="host-penalties">0</span></p>
                            <h5>Goals</h5>
                        </td>
                        <td>
                            <h4 id="visitor-name">Test</h4>
                            <p>Goals: <span id="visitor-goals">0</span></p>
                            <p>Penalties: <span id="visitor-penalties">0</span></p>
                            <h5>Goals</h5>
                        </td>
                    </tr>
                    <tr>
                        <td id="host-goals-list" style="border: 1px black solid; width: 50%; vertical-align: top;">
                            <p>Test, 0</p>
                            <p>Test, 0</p>
                        </td>
                        <td id="visitor-goals-list" style="border: 1px black solid; width: 50%; vertical-align: top;">
                            <p>Test, 0</p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <h5>Penalties</h5>
                        </td>
                        <td>
                            <h5>Penalties</h5>
                        </td>
                    </tr>
                    <tr>
                        <td id="host-penalties-list" style="border: 1px black solid; width: 50%; vertical-align: top;">
                            <p>Test, 2 min</p>
                        </td>
                        <td id="visitor-penalties-list" style="border: 1px black solid; width: 50%; vertical-align: top;">
                            <p>Test, 2 min</p>
                            <p>Test, 2 min</p>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input id="bet-on-host" name="bet-on" type="radio" value="host"> Host
                            <input id="bet-on-visitor" name="bet-on" type="radio" value="visitor"> Visitor
                            <br>
                            Amount: <input id="betAmount" type="number">
                            <button id="btn-bet">Bet</button>
                        </td>
                    </tr>
                </table>
            </div>
        </td>
        <td style="width: 20%; vertical-align: top;">
            <h3>Events</h3>
            <div id="notification-section"/>
        </td>
    </tr>
</table>
</body>
</html>