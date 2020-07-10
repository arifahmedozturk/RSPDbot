package com.example.dynamite

import com.softwire.dynamite.bot.Bot
import com.softwire.dynamite.game.Gamestate
import com.softwire.dynamite.game.Move

class MyBot : Bot {
    var rounds = 0
    var dynamites = 100
    var wins = 0
    fun getCounter(theMove: Move) : Move
    {
        if(theMove == Move.S) return Move.R
        if(theMove == Move.R) return Move.P
        if(theMove == Move.P) return Move.S
        if(theMove == Move.D) return Move.W
        return Move.R
    }
    fun moveToInt(theMove: Move) : Int
    {
        if(theMove == Move.S) return 1
        if(theMove == Move.R) return 2
        if(theMove == Move.P) return 3
        if(theMove == Move.D) return 4
        return 5
    }
    fun intToMove(theVal: Int) : Move
    {
        if(theVal == 1) return Move.S
        if(theVal == 2) return Move.R
        if(theVal == 3) return Move.P
        if(theVal == 4) return Move.D
        return Move.W
    }
    fun getRandomMove(moves:List<Move>):Move
    {
       // val moves = listOf(Move.S,Move.R,Move.P)
        return moves.shuffled().first()
    }
    fun getMovesPerPrediction(gameState: Gamestate,predictionMatrix: Array<Array<Int>>, moves: List<Move>) : Move
    {
        var sum1 = 0
        var maxVal1 = 0
        var maxP1 = 0
        var p1=0
        var noRounds = gameState.rounds.size
        if(noRounds >0 ) {
        p1 = moveToInt(gameState.rounds[noRounds - 1].p1)
        for(i in 1..5)
        {
            sum1+=predictionMatrix[p1][i]
            if(maxVal1 < predictionMatrix[p1][i]) {
                maxVal1 = predictionMatrix[p1][i]
                maxP1 = i
            }
            sum1+=predictionMatrix[p1][i]

        }
        if(sum1/2 - 2< maxVal1) return getCounter(intToMove(maxP1))
        }

        var sum2 = 0
        var maxVal2 = 0
        var maxP2 = 0
        p1=0
        if(noRounds > 1) {
        p1 = moveToInt(gameState.rounds[noRounds - 2].p1)*10 + moveToInt(gameState.rounds[noRounds - 1].p1)
        for(i in 1..5)
        {
            sum1+=predictionMatrix[p1][i]
            if(maxVal2 < predictionMatrix[p1][i]) {
                maxVal2 = predictionMatrix[p1][i]
                maxP2 = i
            }
            sum2+=predictionMatrix[p1][i]

        }
            if(sum2/2 - 2 < maxVal2) return getCounter(intToMove(maxP2))

        }
        var sum3 = 0
        var maxVal3 = 0
        var maxP3 = 0
        p1=0
        if(noRounds > 2) {
            p1 = moveToInt(gameState.rounds[noRounds - 3].p1)*100 + moveToInt(gameState.rounds[noRounds - 2].p1)* 10 + moveToInt(gameState.rounds[noRounds-1].p1)
            for(i in 1..5)
            {
                sum1+=predictionMatrix[p1][i]
                if(maxVal3 < predictionMatrix[p1][i]) {
                    maxVal3 = predictionMatrix[p1][i]
                    maxP3 = i
                }
                sum3+=predictionMatrix[p1][i]

            }
            if(sum3/2 - 2< maxVal3 ) return getCounter(intToMove(maxP3))

        }


        return getRandomMove(moves)

    }
    override fun makeMove(gamestate: Gamestate): Move {
        var moves = mutableListOf(Move.D,Move.P,Move.R,Move.W,Move.S)
        var totalRounds = 0
        var myWins = 0
        var enemyWins = 0
        var lastDraw = 0
        var myDynamites = 100
        var enemyDynamites = 100
        var predictionMatrix = Array(1004){Array(7){0} }
        var myMoves1 = 0
        var myMoves2 = 0
        var myMoves3 = 0
        var draws = 0
        var dynamitesAtDraw = 0
        for(round in gamestate.rounds)
        {
            if(myMoves1 > 0)predictionMatrix[myMoves1][moveToInt(round.p2)]++
            if(myMoves2 > 9) predictionMatrix[myMoves2][moveToInt(round.p2)]++
            if(myMoves3 > 99) predictionMatrix[myMoves3][moveToInt(round.p2)]++
            totalRounds++
            if(round.p1 == Move.D) myDynamites --;
            if(round.p2 == Move.D) enemyDynamites --;
            if(round.p1 == round.p2)
            {
                if(lastDraw == 0) draws++
                ++lastDraw;
            }
            else
            {
                if(lastDraw != 0 && round.p2 == Move.D) dynamitesAtDraw++
                if(round.p2 == Move.D || round.p2 == getCounter(round.p1))
                    enemyWins += lastDraw+1
                else
                    myWins += lastDraw+1
                lastDraw = 0

            }
            myMoves1 = moveToInt(round.p1)
            myMoves2 = (myMoves2*10 + moveToInt(round.p1))%100
            myMoves3 = (myMoves3*10 + moveToInt(round.p1))%1000
        }

        if(lastDraw > 0)
        {
            if(enemyDynamites == 0) moves.remove(Move.W)
             if(draws > 5 && (myDynamites > 0) && draws - dynamitesAtDraw < draws/2) {
            return Move.W
        }
            if(myDynamites > enemyDynamites) return Move.D
            else if(myDynamites == enemyDynamites && myDynamites > 0)
            {
                moves.remove(Move.P)
                moves.remove(Move.R)
                moves.remove(Move.S)
                return getRandomMove(moves)
            }
            moves.remove(Move.D)
            return getRandomMove(moves)
        }
        if(myDynamites == 0) moves.remove(Move.D)
        if(enemyWins - myWins > 50 || enemyDynamites == 0) moves.remove(Move.W)
        if(myWins - enemyWins > 100) moves.remove(Move.D)
        val myList = listOf(1,2,3,4)
        if(myList.shuffled().first() <= 2 || totalRounds < 100) return getRandomMove(moves)
        return getMovesPerPrediction(gamestate,predictionMatrix,moves)
    }

    init {
        // Are you debugging?
        // Put a breakpoint on the line below to see when we start a new match
        println("Started new match")
    }
}