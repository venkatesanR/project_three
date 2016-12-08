package com.techland.training.krishna;

public class SnakeAndLadder {
	public static Player play(Player player) {
		int tempPosition = SnakeLadderUtil.throwDice() + player.getCurrentPostion();
		Integer ladder = SnakeLadderUtil.getLadder(tempPosition);
		Integer slope = SnakeLadderUtil.getSlope(tempPosition);
		if (ladder != null) {
			player.setCurrentPostion(ladder);
		} else if (slope != null) {
			player.setCurrentPostion(slope);
		} else if (tempPosition > 100) {
			player.setCurrentPostion(player.getCurrentPostion());
		} else {
			player.setCurrentPostion(tempPosition);
		}
		return player;
	}

	public static void main(String[] args) {
		Player playerOne = new Player();
		playerOne.setPlayerName("KrishNa");
		Player playerTwo = new Player();
		playerTwo.setPlayerName("Venkat");
		while (true) {
			playerOne = play(playerOne);
			System.out.println("Player " + playerOne.getPlayerName() +" position :"+playerOne.getCurrentPostion());
			if (playerOne.getCurrentPostion() == 100) {
				System.out.println("Player " + playerOne.getPlayerName() + " won");
				break;
			}
			playerTwo = play(playerTwo);
			System.out.println("Player " + playerTwo.getPlayerName() +" position :"+playerTwo.getCurrentPostion());
			if (playerTwo.getCurrentPostion() == 100) {
				System.out.println("Player " + playerTwo.getPlayerName() + " won");
				break;
			}
		}
		System.out.println("Game Over");
	}

}
