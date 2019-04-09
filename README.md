[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
# Incan Gold
## Intro
This is a java program based on swing to mimic the table top board game Incan Gold by Alan R. Moon’s and Bruno Faidutti’s. The players of Incan Gold are adventurers who have chosen to explore an Incan temple rumored to contain many treasures. Of course, the temple is said to be full of dangers too. The player who knows when enough has been gained and still gets out safely will be the ultimate winner. The game can have 3 to 8 players, and each game will take about 7 min to finish. The player can see all the cards of the round and the game process and their scores. Since the program is running on a single machine, player will take turn to make decision. After all the players have decide their action, the program will show whatever the players pick and move the cards accordingly.

## Rules
The game itself contain these competes:
•	5 Temple cards to make the game board 
•	16 Player cards (8 Torch cards and 8 Camp cards) 
•	8 Tent cards, scored to fold in half easily 
•	30 Quest cards, 
•	15 Treasure cards and 
•	15 Hazard cards (3 each of 5 hazards) 
•	5 Artifact cards 
•	110 Treasures
The game is played in five rounds. The player with the most wealth (in treasure value) after the fifth round wins. 
1.	In each turn every player must decide to continue into the temple or take to treasure back to the camp. 
2.	After every player decide, a new card will be draw from the deck. 
For player who choose to stay in the temple:
•	If it is a treasure card the, treasure will be divided equally and placed beside player tent, if the number is not dividable the reminder will be leave on the card and can be collect if a player decides to go back to the camp
•	If the card is an Artifact than the Artifact stays on the path. It’s worth extra points at the end of the game, but only to a person clever enough to get it safely back to camp
•	If the card is a Hazard one of the two will happen:
i.	 If no other Hazard of the same type has been previously revealed, nothing happens.
ii.	If this is the second Hazard card of the same type, the players have been scared away from the Temple and the round is over. All players still in the Temple give back all the treasures they took during the round. This second Hazard card will be removed from the game
For player(s) who decide to leave:
•	They equally divide all the treasures that have been left on any Quest cards in the path for this round. If the number of treasures cannot be divided equally, the extra treasures are left on any Quest card
•	They return to camp and place all the treasures they received during this round inside their Tent. These treasures are safe, and may not be lost.
•	Artifacts. If more than one player leaves, no one gets Artifacts. If one and only one player leaves the Temple at this time, that player may pick up all Artifacts that are on the path.
•	The first three Artifacts to leave the Temple are worth 5 points each. Any further Artifacts that leave the Temple are worth 10 points

3.	End of Round
•	A round can end in two ways: When all players have left the Temple, or when the second Hazard card of the same type is drawn. 
•	If the round ends because a second Hazard card is drawn, this second Hazard card is removed from the game and placed beneath the Temple card representing that round before shuffling the deck for the next round.
•	If there were any Artifacts on the path when the round ends, these treasures are lost forever and removed from the game.
4.	End of Game
•	The game is played in five rounds. After the final round, the Tents are opened and players count their treasures.

## Run
Run IncanGold.jar to start the game
