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
    <script src="scripts/js.cookie.js"></script>
    <script>
        var cptBet = Cookies.get('bet');
        if(cptBet == undefined) {
            cptBet = 0;
            Cookies.set('bet', cptBet, { expires: 7, path: '' });
        }

        var eventSource = new EventSource("servlets/notification");

        var games = new Array();

        eventSource.onmessage = function (event) {
            var notificationSection = $("#notification-section");
            //var obj = jQuery.parseJSON(event.data);
            //TODO
            notificationSection.prepend("<p>" + event.data + "</p>");
            if(notificationSection.children().length > 10)
                notificationSection.children().last().remove();

            alert(event.data);
        };

        eventSource.addEventListener('up_vote', function (event) {
            //TODO
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
            })
                    .done(callback)
                    .fail(function( jqXHR, textStatus, errorThrown ) {
                        alert(textStatus + ": " + errorThrown);
                    });
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
                    betOn = $("#host-name").text();
                if(betOnVisitor)
                    betOn = $("#visitor-name").text();

                post("servlets/placebet", {betOn: betOn, amount: amount, gameID: currentGameID}, function(data) {
                    cptBet = cptBet + 1;
                    Cookies.set('bet', cptBet, { expires: 7, path: '' });
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
            list.text("");
            $.each(goals, function(i, goal) {
                var line = $("<p>");
                line.text(goal.GoalHolder + " - " + goal.amount);
                list.append(line);
            });
        }

        var setPenalties = function(list, penalties) {
            list.text("");
            $.each(penalties, function(i, penalty) {
                var line = $("<p>");
                line.text(penalty.PenaltyHolder + " - " + formatTime(penalty.TimeLeft));
                list.append(line);
            });
        }

        var getGame = function (gameID) {
            $("#btn-refresh").attr("disabled", true);
            $("#btn-refresh").text("Updating");
            clearTimeout(timeoutID);

            var game = null;
            $.each(games, function (i, data) {
                if(data.GameID == gameID)
                    game = data;
            });

            get("servlets/gameinfo", {GameID: gameID}, function (data) {
                gameSection.removeClass("hidden");

                var gameInfo = data;//jQuery.parseJSON(data);
                $("#game-section-title").text(game.Host + " vs " + game.Visitor);
                $("#host-name").text(game.Host);
                $("#visitor-name").text(game.Visitor);
                $("#period").text(gameInfo.Period);
                $("#period-chronometer").text(formatTime(gameInfo.PeriodChronometer));
                $("#host-goals").text(gameInfo.HostGoalsTotal);
                $("#visitor-goals").text(gameInfo.VisitorGoalsTotal);

                setGoals($("#host-goals-list"), gameInfo.HostGoals);
                setGoals($("#visitor-goals-list"), gameInfo.VisitorGoals);
                setPenalties($("#host-penalties-list"), gameInfo.HostPenalties);
                setPenalties($("#visitor-penalties-list"), gameInfo.VisitorPenalties);

                $("#btn-refresh").text("Refresh");
                $("#btn-refresh").attr("disabled", false);
                timeoutID = setTimeout(refresh, 120000);
            });
        }

        get("servlets/gamelist", {}, function (data) {
            games = data;

            $.each(games, function (i, game) {
                games.push(game);

                var btnGame = $("<button>");
                btnGame.text(game.Host + " vs " + game.Visitor);
                btnGame.click(function () {
                    currentGameID = game.GameID;
                    getGame(game.GameID);
                });
                $("#games-section").append(btnGame);
            });

            if(cptBet > 0) {
                get("betResults", {}, function(data) {
                    $.each(data, function(i, obj) {
                        cptBet = cptBet - 1;
                        Cookies.set('bet', cptBet, { expires: 7, path: '' });
                        //TODO
                    })
                });
            }
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
                            Amount: <input id="betAmount" type="number" min="0">
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