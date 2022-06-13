package cli.command;

import app.AppConfig;
import cli.CLIParser;
import servent.SimpleServentListener;

public class HaltCommand implements CLICommand {

	private final CLIParser parser;
	private final SimpleServentListener listener;
	
	public HaltCommand(CLIParser parser, SimpleServentListener listener) {
		this.parser = parser;
		this.listener = listener;
	}
	
	@Override
	public String commandName() {
		return "halt";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Halting...");
		parser.stop();
		listener.stop();
	}

}
