package cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import app.AppConfig;
import app.Cancellable;
import cli.command.*;
import servent.SimpleServentListener;

/**
 * A simple CLI parser. Each command has a name and arbitrary arguments.
 * 
 * Currently supported commands:
 * 
 * <ul>
 * <li><code>info</code> - prints information about the current node</li>
 * <li><code>pause [ms]</code> - pauses exection given number of ms - useful when scripting</li>
 * <li><code>ping [id]</code> - sends a PING message to node [id] </li>
 * <li><code>broadcast [text]</code> - broadcasts the given text to all nodes</li>
 * <li><code>causal_broadcast [text]</code> - causally broadcasts the given text to all nodes</li>
 * <li><code>print_causal</code> - prints all received causal broadcast messages</li>
 * <li><code>halt</code> - halts the servent and program finishes</li>
 * </ul>
 * 
 * @author bmilojkovic
 *
 */
public class CLIParser implements Runnable, Cancellable {

	private volatile boolean working = true;
	
	private final List<CLICommand> commandList;

	private Scanner scanner;
	
	public CLIParser(SimpleServentListener listener) {
		this.commandList = new ArrayList<>();
		
		commandList.add(new InfoCommand());
		commandList.add(new PauseCommand());
		commandList.add(new StartCommand(this));
		commandList.add(new HaltCommand(this, listener));
		commandList.add(new ResultCommand());
		commandList.add(new StopCommand());
		commandList.add(new StatusCommand());
		commandList.add(new QuitCommand(this, listener));
		commandList.add(new DieCommand(this, listener));
	}
	
	@Override
	public void run() {
		scanner = new Scanner(System.in);
		
		while (working) {
			String commandLine = scanner.nextLine();
			
			int spacePos = commandLine.indexOf(" ");
			
			String commandName;
			String commandArgs = null;
			if (spacePos != -1) {
				commandName = commandLine.substring(0, spacePos);
				commandArgs = commandLine.substring(spacePos+1);
			} else {
				commandName = commandLine;
			}
			
			boolean found = false;
			
			for (CLICommand cliCommand : commandList) {
				if (cliCommand.commandName().equals(commandName)) {
					cliCommand.execute(commandArgs);
					found = true;
					break;
				}
			}
			
			if (!found) {
				AppConfig.timestampedErrorPrint("Unknown command: " + commandName);
			}
		}

		scanner.close();
	}

	public Scanner getScanner() {
		return scanner;
	}

	@Override
	public void stop() {
		this.working = false;
		
	}
}
