<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>HockeyLive</title>
    <link rel="stylesheet" href="styles/index.css"/>
    <link rel="stylesheet" href="styles/timed-message.css"/>
    <script src="scripts/jquery-2.1.4.min.js"></script>
    <script src="scripts/js.cookie.js"></script>
    <script src="scripts/timed-message.js"></script>
    <script>

        function jsonStringnify(array) {
            var string = "[";
            $.each(array, function(i,element) {
                string = string + element;
                if(i < array.length -1)
                    string = string + ",";
            });
            string = string + "]";
            return string;
        }

        function arrayRemove(array, element) {
            var index = array.indexOf(element);
            if (index > -1)
                array = array.splice(index, 1);

            return array;
        }

        var arrayBet = Cookies.get('bet');
        if (arrayBet == undefined) {
            arrayBet = [];
            Cookies.set('bet', jsonStringnify(arrayBet), {expires: 7, path: ''});
        } else {
            arrayBet = $.parseJSON(arrayBet);
        }

        var eventSource = new EventSource("servlets/notification");

        var games = new Array();

        function showNotification(message) {
            notificationSection.prepend("<p>" + message + "</p>");
            if (notificationSection.children().length > 10)
                notificationSection.children().last().remove();

            TimedMessage.createMessage(message);
        }

        eventSource.onmessage = function (event) {
            var notificationSection = $("#notification-section");
            var hockeyEvent = jQuery.parseJSON(event.data);

            showNotification(hockeyEvent.description);
        };

        eventSource.addEventListener('bet-result', function (event) {
            var hockeyEvent = jQuery.parseJSON(event.data);

            arrayBet = arrayRemove(arrayBet, hockeyEvent.betID);
            Cookies.set('bet', jsonStringnify(arrayBet), {expires: 7, path: ''});

            showNotification(hockeyEvent.description);
        }, false);

        function post(url, data, callback) {
            $.post(url, data)
                    .done(callback)
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        alert(textStatus + ": " + errorThrown);
            });
        }

        function get(url, data, callback) {
            $.ajax({
                        method: "GET",
                        url: url,
                        data: data
                    })
                    .done(callback)
                    .fail(function (jqXHR, textStatus, errorThrown) {
                        alert(textStatus + ": " + errorThrown);
                    });
        }

        var timeoutID = null;
        var currentGameID = null;

        function refresh() {
            getGame(currentGameID);
        }

        function bet() {
            var betOnHost = $("#bet-on-host:checked").length > 0;
            var betOnVisitor = $("#bet-on-visitor:checked").length > 0;
            var amount = $("#bet-amount")[0].value;

            if (amount != "" && (betOnHost || betOnVisitor)) {
                var betOn;
                if (betOnHost)
                    betOn = $("#host-name").text();
                if (betOnVisitor)
                    betOn = $("#visitor-name").text();

                post("servlets/placebet", {teamName: betOn, amount: amount, GameID: currentGameID}, function (data) {
                    arrayBet.push(data.ID);
                    Cookies.set('bet', arrayBet, {expires: 7, path: ''});
                    TimedMessage.createMessage("Confirmation number : " + data.ID);
                });
            }
        }

        function formatTime(timeInSeconds) {
            return Math.floor(timeInSeconds / 60) + ":" + ("0" + timeInSeconds % 60).slice(-2);
        }

        function setGoals(list, goals) {
            list.text("");
            $.each(goals, function (i, goal) {
                var line = $("<p>");
                line.text(goal.GoalHolder + " - " + goal.amount);
                list.append(line);
            });
        }

        function setPenalties(list, penalties) {
            list.text("");
            $.each(penalties, function (i, penalty) {
                var line = $("<p>");
                line.text(penalty.PenaltyHolder + " - " + formatTime(penalty.TimeLeft));
                list.append(line);
            });
        }

        function getGame(gameID) {
            $("#btn-refresh").attr("disabled", true);
            $("#btn-refresh").text("Updating");
            clearTimeout(timeoutID);

            var game = null;
            $.each(games, function (i, data) {
                if (data.GameID == gameID)
                    game = data;
            });

            get("servlets/gameinfo", {GameID: gameID}, function (data) {
                $("#game-section").removeClass("hidden");

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

                timeoutID = setTimeout(refresh, 120000);
                setTimeout(function () {
                    $("#btn-refresh").text("Refresh");
                    $("#btn-refresh").attr("disabled", false);
                }, 2000);
            });
        }

        function getGames() {
            get("servlets/gamelist", {}, function (data) {
                games = data;

                $.each(games, function (i, game) {
                    var btnGame = $("<a>");
                    btnGame.text(game.Host + " vs " + game.Visitor);
                    btnGame.click(function () {
                        currentGameID = game.GameID;
                        getGame(game.GameID);
                    });
                    $("#games-section").append(btnGame).append("<br/>");
                });

                if (arrayBet.length > 0) {
                    $.each(arrayBet, function (i, betID) {
                        get("servlets/betresult", {BetID: betID}, function (bet) {
                            arrayBet = arrayRemove(arrayBet, betID);
                            Cookies.set('bet', arrayBet, {expires: 7, path: ''});

                            showNotification("Bet for: "+bet.betOn+"  Amount gained: "+bet.amountGained.toFixed(2)+"$ Amount betted: "+bet.amount.toFixed(2)+"$");
                        });
                    })
                }
            });
        }

        $(document).ready(function () {
            getGames();

            $("#btn-bet").click(bet);
            $("#btn-refresh").click(refresh);
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
                        <td id="visitor-penalties-list"
                            style="border: 1px black solid; width: 50%; vertical-align: top;">
                            <p>Test, 2:00</p>

                            <p>Test, 2:00</p>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input id="bet-on-host" name="bet-on" type="radio" value="host"> Host
                            <input id="bet-on-visitor" name="bet-on" type="radio" value="visitor"> Visitor
                            <br>
                            Amount: <input id="bet-amount" type="number" min="0">
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

<div id='notification' class="notification" style="display: none"></div>
</body>
</html>