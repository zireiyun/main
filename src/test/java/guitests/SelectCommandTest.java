package guitests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import seedu.address.model.task.ReadOnlyTask;

public class SelectCommandTest extends ToDoListGuiTest {


    @Test
    public void selectPerson_nonEmptyList() {

        assertSelectionInvalid(10); // invalid index
        assertNoTaskSelected();

        assertSelectionSuccess(1); // first person in the list
        int taskCount = td.getTypicalTasks().length;
        assertSelectionSuccess(taskCount); // last person in the list
        int middleIndex = taskCount / 2;
        assertSelectionSuccess(middleIndex); // a person in the middle of the list

        assertSelectionInvalid(taskCount + 1); // invalid index
        assertTaskSelected(middleIndex); // assert previous selection remains

        /* Testing other invalid indexes such as -1 should be done when testing the SelectCommand */
    }

    @Test
    public void selectTask_emptyList() {
        commandBox.runCommand("clear");
        assertListSize(0);
        assertSelectionInvalid(1); //invalid index
    }

    private void assertSelectionInvalid(int index) {
        commandBox.runCommand("select " + index);
        assertResultMessage("The task index provided is invalid");
    }

    private void assertSelectionSuccess(int index) {
        commandBox.runCommand("select " + index);
        assertResultMessage("Selected Task: " + index);
        assertTaskSelected(index);
    }

    private void assertTaskSelected(int index) {
        assertEquals(taskListPanel.getSelectedPersons().size(), 1);
        ReadOnlyTask selectedTask = taskListPanel.getSelectedPersons().get(0);
        assertEquals(taskListPanel.getPerson(index - 1), selectedTask);
        //TODO: confirm the correct page is loaded in the Browser Panel
    }

    private void assertNoTaskSelected() {
        assertEquals(taskListPanel.getSelectedPersons().size(), 0);
    }

}
