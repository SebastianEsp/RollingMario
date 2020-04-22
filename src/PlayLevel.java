import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import engine.core.MarioGame;
import engine.core.MarioResult;

public class PlayLevel {
    public static void printResults(MarioResult result) {
	System.out.println("****************************************************************");
	System.out.println("Game Status: " + result.getGameStatus().toString() + 
		" Percentage Completion: " + result.getCompletionPercentage());
	System.out.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() + 
		" Remaining Time: " + (int)Math.ceil(result.getRemainingTime() / 1000f)); 
	System.out.println("Mario State: " + result.getMarioMode() +
		" (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
	System.out.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() + 
		" Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() + 
		" Falls: " + result.getKillsByFall() + ")");
	System.out.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() + 
		" Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
	System.out.println("****************************************************************");
    }
    
    public static String getLevel(String filepath) {
	String content = "";
	try {
	    content = new String(Files.readAllBytes(Paths.get(filepath)));
	} catch (IOException e) {
	}
	return content;
    }

	public static void main(String[] args) {
		MarioGame game = new MarioGame();
		// printResults(game.playGame(getLevel("levels/original/lvl-1.txt"), 200, 0));
		try{
			PrintWriter writer = new PrintWriter("Results.txt");
			writer.print("");
			writer.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		for (int i = 0; i < 3; i++) {
			var results = game.runGame(new agents.rollingHorizon.Agent(), getLevel("levels/original/lvl-1.txt"), 20, 0, true);
			printResults(results);

			File file = new File("Results.txt");
			try {
				FileWriter fr = new FileWriter(file, true);
				fr.write("RUN: " + (i + 1) + "\n");
				fr.write("GAME STATE: " + results.getGameStatus() + "\n");
				fr.write("PERCENTAGE COMPLETION: " + results.getCompletionPercentage()+ "\n");
				fr.write("TIME REMAINING: " + results.getRemainingTime() + "\n");
				fr.write("COINS: " + results.getCurrentCoins() + "\n");
				fr.write("STOMPS: " + results.getKillsByStomp() + "\n");
				fr.write("JUMPS: " + results.getNumJumps() + "\n");
				fr.write("---------------------------------------------------------------------" + "\n");
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//printResults(game.runGame(new agents.robinBaumgarten.Agent(), getLevel("levels/original/lvl-1.txt"), 20, 0, true));
	}
}
