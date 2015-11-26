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

        var games = new Array();

        eventSource.onmessage = function (event) {
            var notificationSection = $("#notification-section");
            //var obj = jQuery.parseJSON(event.data);

            notificationSection.prepend("<p>" + event.data + "</p>");
            if(notificationSection.children().length > 10)
                notificationSection.children().last().remove();

            alert(event.data);
        };

        eventSource.addEventListener('up_vote', function (event) {
            $("#notification-section").prepend("<p>" + event.data + "</p>");
        }, false);

        var gameSection = $("#game-section");

        var post = function (url, data, callback) {
            $.post(url, data).done(callback);
        }

        var get = function (url, data, callback) {
            $.ajax({
                method: "GET",
                url: url,
                data: data
            }).done(callback);
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
                var betOn;
                if(betOnHost)
                    betOn = $("#host-name").innerHTML;
                if(betOnVisitor)
                    betOn = $("#visitor-name").innerHTML;

                post("bet", {betOn: betOn, amount: amount, gameID: currentGameID}, function(data) {
                    alert(data);
                });
            }
        }

        $("#btn-bet").click(bet);
        $("#btn-refresh").click(refresh);

        var formatTime = function(timeInSeconds) {
            return timeInSeconds / 60 + ":" + ("0" + timeInSeconds % 60).slice(-2);
        }

        var setGoals = function(list, goals) {
            list.innerHTML = "";
            $.each(goals, function(i, goal) {
                var line = $("<p>");
                line.innerHTML = goal.GoalHolder + " - " + goal.amount;
                list.append(line);
            });
        }

        var setPenalties = function(list, penalties) {
            list.innerHTML = "";
            $.each(penalties, function(i, penalty) {
                var line = $("<p>");
                line.innerHTML = penalty.PenaltyHolder + " - " + formatTime(penalty.TimeLeft);
                list.append(line);
            });
        }

        var getGame = function (gameID) {
            $("#btn-refresh").attr("disabled", true);
            $("#btn-refresh").innerHTML = "Updating";
            clearTimeout(timeoutID);

            var game = null;
            $.each(games, function (i, data) {
                if(data.GameID == gameID)
                    game = data;
            });

            get("getGame", {GameID: gameID}, function (data) {
                gameSection.removeClass("hidden");

                var gameInfo = jQuery.parseJSON(data);
                $("#game-section-title").innerHTML = game.Host + " vs " + game.Visitor;
                $("#host-name").innerHTML = game.Host;
                $("#visitor-name").innerHTML = game.Visitor;
                $("#period").innerHTML = gameInfo.Period;
                $("#period-chronometer").innerHTML = formatTime(gameInfo.PeriodChronometer);
                $("#host-goals").innerHTML = gameInfo.HostGoalsTotal;
                $("#visitor-goals").innerHTML = gameInfo.VisitorGoalsTotal;

                setGoals($("#host-goals-list"), gameInfo.HostGoals);
                setGoals($("#visitor-goals-list"), gameInfo.VisitorGoals);
                setPenalties($("#host-penalties-list"), gameInfo.HostPenalties);
                setPenalties($("#visitor-penalties-list"), gameInfo.VisitorPenalties);

                $("#btn-refresh").innerHTML = "Refresh";
                $("#btn-refresh").attr("disabled", false);
                timeoutID = setTimeout(refresh, 120000);
            });
        }

        get("getGames", {}, function (data) {
            games = jQuery.parseJSON(data);

            $.each(games, function (i, data) {
                games.push(data);

                var btnGame = $("<button>");
                btnGame.innerHTML = data.Host + " vs " + data.Visitor;
                btnGame.click(function () {
                    getGame(data.GameID);
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
                <button id="btn-refresh">Refresh</button>

                <p>Period <span id="period">0</span> - <span id="period-chronometer">0:00</span></p>
                <table style="width: 100%">
                    <tr>
                        <td>
                            <h4 id="host-name">Test</h4>
                            <p>Goals: <span id="host-goals">0</span></p>
                            <h5>Goals</h5>
                        </td>
                        <td>
                            <h4 id="visitor-name">Test</h4>
                            <p>Goals: <span id="visitor-goals">0</span></p>
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
                            <p>Test, 2:00</p>
                        </td>
                        <td id="visitor-penalties-list" style="border: 1px black solid; width: 50%; vertical-align: top;">
                            <p>Test, 2:00</p>
                            <p>Test, 2:00</p>
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