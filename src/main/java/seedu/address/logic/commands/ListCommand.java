package seedu.address.logic.commands;


/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list_completed";

    public static final String MESSAGE_SUCCESS = "Listed all completed tasks";


    @Override
    public CommandResult execute() {
        model.updateFilteredListToShowCompleted();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
