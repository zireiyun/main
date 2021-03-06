# doitdoit!! - Developer Guide

By : `Team Just Do It`  &nbsp;&nbsp;&nbsp;&nbsp; Since: `March 2017`  &nbsp;&nbsp;&nbsp;&nbsp; Licence: `MIT`

---

1. [Setting Up](#setting-up)
2. [Design](#design)
3. [Implementation](#implementation)
4. [Testing](#testing)
5. [Dev Ops](#dev-ops)

* [Appendix A: User Stories](#appendix-a--user-stories)
* [Appendix B: Use Cases](#appendix-b--use-cases)
* [Appendix C: Non Functional Requirements](#appendix-c--non-functional-requirements)
* [Appendix D: Glossary](#appendix-d--glossary)
* [Appendix E : Product Survey](#appendix-e--product-survey)


## 1. Setting up

### 1.1. Prerequisites

1. **JDK `1.8.0_60`**  or later<br>

    > Having any Java 8 version is not enough. <br>
    This app will not work with earlier versions of Java 8.

2. **Eclipse** IDE
3. **e(fx)clipse** plugin for Eclipse (Do the steps 2 onwards given in
   [this page](http://www.eclipse.org/efxclipse/install.html#for-the-ambitious))
4. **Buildship Gradle Integration** plugin from the Eclipse Marketplace
5. **Checkstyle Plug-in** plugin from the Eclipse Marketplace


### 1.2. Importing the project into Eclipse

0. Fork this repo, and clone the fork to your computer
1. Open Eclipse (Note: Ensure you have installed the **e(fx)clipse** and **buildship** plugins as given
   in the prerequisites above)
2. Click `File` > `Import`
3. Click `Gradle` > `Gradle Project` > `Next` > `Next`
4. Click `Browse`, then locate the project's directory
5. Click `Finish`

  > * If you are asked whether to 'keep' or 'overwrite' config files, choose to 'keep'.
  > * Depending on your connection speed and server load, it can even take up to 30 minutes for the set up to finish
      (This is because Gradle downloads library files from servers during the project set up process)
  > * If Eclipse auto-changed any settings files during the import process, you can discard those changes.

### 1.3. Configuring Checkstyle
1. Click `Project` -> `Properties` -> `Checkstyle` -> `Local Check Configurations` -> `New...`
2. Choose `External Configuration File` under `Type`
3. Enter an arbitrary configuration name e.g. MyToDoList
4. Import checkstyle configuration file found at `config/checkstyle/checkstyle.xml`
5. Click OK once, go to the `Main` tab, use the newly imported check configuration.
6. Tick and select `files from packages`, click `Change...`, and select the `resources` package
7. Click OK twice. Rebuild project if prompted

> Note to click on the `files from packages` text after ticking in order to enable the `Change...` button

### 1.4. Troubleshooting project setup

**Problem: Eclipse reports compile errors after new commits are pulled from Git**

* Reason: Eclipse fails to recognize new files that appeared due to the Git pull.
* Solution: Refresh the project in Eclipse:<br>
  Right click on the project (in Eclipse package explorer), choose `Gradle` -> `Refresh Gradle Project`.

**Problem: Eclipse reports some required libraries missing**

* Reason: Required libraries may not have been downloaded during the project import.
* Solution: [Run tests using Gradle](UsingGradle.md) once (to refresh the libraries).


## 2. Design

### 2.1. Architecture

<img src="images/Architecture.png" width="600"><br>
_Figure 2.1.1 : Architecture Diagram_

The **_Architecture Diagram_** given above explains the high-level design of doitdoit!!.
Given below is a quick overview of each component.

`Main` has only one class called [`MainApp`](../src/main/java/seedu/address/MainApp.java). It is responsible for,

* At app launch: Initializes the components in the correct sequence, and connects them with each other.
* At shut down: Shuts down components and invokes cleanup method where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.
Two of those classes play important roles at the architecture level.

* `EventsCenter` : This class (written using [Google's Event Bus library](https://github.com/google/guava/wiki/EventBusExplained))
  is used by components to communicate with other components using events (i.e. a form of _Event Driven_ design)
* `LogsCenter` : Used by many classes to write log messages to the App's log file.

The rest of the App consists of four components.

* [**`UI`**](#ui-component) : The UI of the App.
* [**`Logic`**](#logic-component) : The command executor.
* [**`Model`**](#model-component) : Holds the data of the App in-memory.
* [**`Storage`**](#storage-component) : Reads data from, and writes data to, the hard disk.

Each of the four components

* Defines its _API_ in an `interface` with the same name as the Component.
* Exposes its functionality using a `{Component Name}Manager` class.

For example, the `Logic` component (see the class diagram given below) defines it's API in the `Logic.java`
interface and exposes its functionality using the `LogicManager.java` class.<br>
<img src="images/LogicClassDiagram.png" width="800"><br>
_Figure 2.1.2 : Class Diagram of the Logic Component_

#### Events-Driven nature of the design

The _Sequence Diagram_ below shows how the components interact for the scenario where the user issues the
command `delete 1`.

<img src="images\SDforDeleteTask.png" width="800"><br>
_Figure 2.1.3a : Component interactions for `delete 1` command (part 1)_

>The `Model` simply raises a `ToDoListChangedEvent` when the doitdoit!! data are changed,
 instead of asking the `Storage` to save the updates to the hard disk.

The diagram below shows how the `EventsCenter` reacts to that event, which eventually results in the updates
being saved to the hard disk and the status bar of the UI being updated to reflect the 'Last Updated' time. <br>
<img src="images\SDforDeleteTaskEventHandling.png" width="800"><br>
_Figure 2.1.3b : Component interactions for `delete 1` command (part 2)_

> The event is propagated through the `EventsCenter` to the `Storage` and `UI` without `Model` having
  to be coupled to either of them. This is an example of how this Event Driven approach helps us reduce direct
  coupling between components.

The sections below give more details of each component.

### 2.2. UI component

<img src="images/UiClassDiagram.png" width="800"><br>
_Figure 2.2.1 : Structure of the UI Component_

**API** : [`Ui.java`](../src/main/java/seedu/address/ui/Ui.java)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `TaskListPanel`,
`StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class.

The `UI` component uses JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files
 that are in the `src/main/resources/view` folder.<br>
 For example, the layout of the [`MainWindow`](../src/main/java/seedu/address/ui/MainWindow.java) is specified in
 [`MainWindow.fxml`](../src/main/resources/view/MainWindow.fxml)

The `UI` component,

* Executes user commands using the `Logic` component.
* Binds itself to some data in the `Model` so that the UI can auto-update when data in the `Model` change.
* Responds to events raised from various parts of the App and updates the UI accordingly.

### 2.3. Logic component

<img src="images/LogicClassDiagram.png" width="800"><br>
_Figure 2.3.1 : Structure of the Logic Component_

**API** : [`Logic.java`](../src/main/java/seedu/address/logic/Logic.java)

1. `Logic` uses the `Parser` class to parse the user command.
2. This results in a `Command` object which is executed by the `LogicManager`.
3. The command execution can affect the `Model` (e.g. adding a task) and/or raise events.
4. The result of the command execution is encapsulated as a `CommandResult` object which is passed back to the `Ui`.

Given below is the Sequence Diagram for interactions within the `Logic` component for the `execute("delete 1")`
 API call.<br>
<img src="images/DeleteTaskSdForLogic.png" width="800"><br>
_Figure 2.3.1 : Interactions Inside the Logic Component for the `delete 1` Command_

### 2.4. Model component

<img src="images/ModelClassDiagram.png" width="800"><br>
_Figure 2.4.1 : Structure of the Model Component_

**API** : [`Model.java`](../src/main/java/seedu/address/model/Model.java)

The `Model`,

* stores a `UserPref` object that represents the user's preferences.
* stores the To Do List data.
* exposes a `UnmodifiableObservableList<ReadOnlyTask>` that can be 'observed' e.g. the UI can be bound to this list
  so that the UI automatically updates when the data in the list change.
* does not depend on any of the other three components.

### 2.5. Storage component

<img src="images/StorageClassDiagram.png" width="800"><br>
_Figure 2.5.1 : Structure of the Storage Component_

**API** : [`Storage.java`](../src/main/java/seedu/address/storage/Storage.java)

The `Storage` component,

* can save `UserPref` objects in tim format and read it back.
* can save doitdoit!! data in xml format and read it back.

### 2.6. Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

## 3. Implementation

### 3.1. Logging

We are using `java.util.logging` package for logging. The `LogsCenter` class is used to manage the logging levels
and logging destinations.

* The logging level can be controlled using the `logLevel` setting in the configuration file
  (See [Configuration](#configuration))
* The `Logger` for a class can be obtained using `LogsCenter.getLogger(Class)` which will log messages according to
  the specified logging level
* Currently log messages are output through: `Console` and to a `.log` file.

**Logging Levels**

* `SEVERE` : Critical problem detected which may possibly cause the termination of the application
* `WARNING` : Can continue, but with caution
* `INFO` : Information showing the noteworthy actions by the App
* `FINE` : Details that is not usually noteworthy but may be useful in debugging
  e.g. print the actual list instead of just its size

### 3.2. Configuration

Certain properties of the application can be controlled (e.g App name, logging level) through the configuration file
(default: `config.tim`):

### 3.3. Use of Design Patterns

Design patterns are used frequently in implementation of doitdoit!!.

* Singleton pattern: Only one undo command stack is needed in doitdoit!!
* Facade Pattern: Creating a start and end time of task requires API of Natty Date Parser. However, as we do not want Deadline and StartTime class to fully access all functionalities of Natty Date Parser, TimeUtil class was created so Deadline and StartTime Class can only access essential functions through TimeUtil.
* Observer Pattern: Storage observes changes in model and updates and saves accordingly.
* Command Pattern: User input are processed, parsed and executed as commands.
* Model-View-Controller (MVC) Pattern: UI acts as the view, Model as the model and Logic as the controller.

## 4. Testing

Tests can be found in the `./src/test/java` folder.

**In Eclipse**:

* To run all tests, right-click on the `src/test/java` folder and choose
  `Run as` > `JUnit Test`
* To run a subset of tests, you can right-click on a test package, test class, or a test and choose
  to run as a JUnit test.

**Using Gradle**:

* See [UsingGradle.md](UsingGradle.md) for how to run tests using Gradle.

We have two types of tests:

1. **GUI Tests** - These are _System Tests_ that test the entire App by simulating user actions on the GUI.
   These are in the `guitests` package.

2. **Non-GUI Tests** - These are tests not involving the GUI. They include,
   1. _Unit tests_ targeting the lowest level methods/classes. <br>
      e.g. `seedu.address.commons.UrlUtilTest`
   2. _Integration tests_ that are checking the integration of multiple code units
     (those code units are assumed to be working).<br>
      e.g. `seedu.address.storage.StorageManagerTest`
   3. Hybrids of unit and integration tests. These test are checking multiple code units as well as
      how the are connected together.<br>
      e.g. `seedu.address.logic.LogicManagerTest`

#### Headless GUI Testing
Thanks to the [TestFX](https://github.com/TestFX/TestFX) library we use,
 our GUI tests can be run in the _headless_ mode.
 In the headless mode, GUI tests do not show up on the screen.
 That means the developer can do other things on the Computer while the tests are running.<br>
 See [UsingGradle.md](UsingGradle.md#running-tests) to learn how to run tests in headless mode.

### 4.1. Troubleshooting tests

 **Problem: Tests fail because NullPointException when AssertionError is expected**

 * Reason: Assertions are not enabled for JUnit tests.
   This can happen if you are not using a recent Eclipse version (i.e. _Neon_ or later)
 * Solution: Enable assertions in JUnit tests as described
   [here](http://stackoverflow.com/questions/2522897/eclipse-junit-ea-vm-option). <br>
   Delete run configurations created when you ran tests earlier.

## 5. Dev Ops

### 5.1. Build Automation

See [UsingGradle.md](UsingGradle.md) to learn how to use Gradle for build automation.

### 5.2. Continuous Integration

We use [Travis CI](https://travis-ci.org/) and [AppVeyor](https://www.appveyor.com/) to perform _Continuous Integration_ on our projects.
See [UsingTravis.md](UsingTravis.md) and [UsingAppVeyor.md](UsingAppVeyor.md) for more details.

### 5.3. Publishing Documentation

See [UsingGithubPages.md](UsingGithubPages.md) to learn how to use GitHub Pages to publish documentation to the
project site.

### 5.4. Making a Release

Here are the steps to create a new release.

 1. Generate a JAR file [using Gradle](UsingGradle.md#creating-the-jar-file).
 2. Tag the repo with the version number. e.g. `v0.1`
 2. [Create a new release using GitHub](https://help.github.com/articles/creating-releases/)
    and upload the JAR file you created.

### 5.5. Converting Documentation to PDF format

We use [Google Chrome](https://www.google.com/chrome/browser/desktop/) for converting documentation to PDF format,
as Chrome's PDF engine preserves hyperlinks used in webpages.

Here are the steps to convert the project documentation files to PDF format.

 1. Make sure you have set up GitHub Pages as described in [UsingGithubPages.md](UsingGithubPages.md#setting-up).
 1. Using Chrome, go to the [GitHub Pages version](UsingGithubPages.md#viewing-the-project-site) of the
    documentation file. <br>
    e.g. For [UserGuide.md](UserGuide.md), the URL will be `https://github.com/CS2103JAN2017-W10-B2/main/blob/master/docs/UserGuide.md`.
 1. Click on the `Print` option in Chrome's menu.
 1. Set the destination to `Save as PDF`, then click `Save` to save a copy of the file in PDF format. <br>
    For best results, use the settings indicated in the screenshot below. <br>
    <img src="images/chrome_save_as_pdf.png" width="300"><br>
    _Figure 5.4.1 : Saving documentation as PDF files in Chrome_

### 5.6. Managing Dependencies

A project often depends on third-party libraries. For example, To Do List depends on the
[Jackson library](http://wiki.fasterxml.com/JacksonHome) for XML parsing. Managing these _dependencies_
can be automated using Gradle. For example, Gradle can download the dependencies automatically, which
is better than these alternatives.<br>
a. Include those libraries in the repo (this bloats the repo size)<br>
b. Require developers to download those libraries manually (this creates extra work for developers)<br>

## Appendix A : User Stories

Priorities: High (must have) - `* * *`, Medium (nice to have)  - `* *`,  Low (unlikely to have) - `*`


Priority | As a ... | I want to ... | So that I can...
-------- | :-------- | :--------- | :-----------
`* * *` | new user | see usage instructions | refer to instructions when I forget how to use the App
`* * *` | user | delete a task | remove entries that I no longer need
`* * *` | user | add a new task with/out a deadline
`* * *` | user | display all uncompleted task then choose one to view
`* * *` | user | find a task by name | locate tasks without having to go through the entire list
`* *` | user | edit details of a task | keep up with changes in my schedule
`* *` | user | be able to view tasks based on closest deadlines | prioritize tasks to complete first
`*` | user | display all completed task then choose one to view | keep track of things I have done
`*` | user | be able to categorise tasks | find similar task easily
`*` | user | specify recurring tasks | deal with tasks that repeats without having to add them repeatedly
`*` | user | add remarks | easily refer to details of a task
`*` | forgetful user | be notified of tasks due soon | complete task before deadline
`*` | user | be able to search for tasks based on certain keywords  | in the case where I do not remember much details of a task
`*` | user | be able to set a task to be 'finished', but not deleted
`*` | user | be able to restart a task that is finished
`*` | user | be able to send a task to others through email or text message
`*` | user | be able to undo operation

## Appendix B : Use Cases

(For all use cases below, the **System** is the `doitdoit!!` and the **Actor** is the `user`, unless specified otherwise)

#### Use case: Add a new task

**MSS**

1. User inputs the add command along with relevant task information
2. Task added to doitdoit!!.
Use case ends.

**Extensions**

1a. Input error

> User guide on adding new task displayed
  Use case ends

#### Use case: Delete task

**MSS**

1. User requests to list task
2. doitdoit!! shows a list of tasks
3. User requests to delete a specific task in the list
4. doitdoit!! deletes the task <br>
Use case ends.

**Extensions**

2a. The list is empty

> Use case ends

3a. The given index is invalid

> 3a1. doitdoit!! shows an error message <br>
  Use case resumes at step 2

#### Use case: Complete task

**MSS**

1. User requests to list task
2. doitdoit!! shows a list of task
3. User requests to mark a specific task in the list as complete
4. doitdoit!! marks the task as complete <br>
Use case ends.

**Extensions**

2a. The list is empty

> Use case ends

3a. The given index is invalid

> 3a1. doitdoit!! shows an error message <br>
  Use case resumes at step 2

#### Use case: List tasks

**MSS**

1. User request to list tasks. (ongoing task shown only)
2. doitdoit!! lists ongoing tasks by deadline.

**Extensions**

1a. Able to list task by options of on-going or completed task.

#### Use case: Edit a Task

**MSS**

1. User requests to list tasks
2. doitdoit!! shows a list of tasks
3. User requests to edit a specific task in the list
4. Task updated

**Extensions**

2a. The list is empty

> Use case ends

3a. The given index is invalid

> 3a1. ToDoList shows an error message <br>
  Use case resumes at step 2

3b. User did not specify any field to be changed.

> 3a1. ToDoList shows an error message <br>
  Use case resumes at step 2

#### Use case: Find a Task by description

**MSS**

1. User requests find a task by inputting keywords.
2. doitdoit!! shows a list of tasks that has title and/or remarks containing a substring of any of the keywords.

**Extensions**

2a. The list is empty

> Use case ends

#### Use case: Find a Task by label

**MSS**

1. User requests find a task by inputting label keywords.
2. doitdoit!! shows a list of tasks that has label matching any of the keywords.

**Extensions**

2a. The list is empty

> Use case ends

#### Use case: Set storage location

**MSS**

1. User specifies new storage file path.
2. doitdoit!! updates storage file path.

#### Use case: Undo an action

**MSS**

1. User requests to undo previous action.
2. doitdoit!! reverts previous action.

**Extensions**

2a. Undo stack is empty (no action to undo)
> 2a1. doitdoit!! shows an error message <br>
  Use case ends

2b. More than 11 undos are called consecutively

> 2b1. doitdoit!! shows an error message <br>
  Use case ends

## Appendix C : Non Functional Requirements

1. Should work on any [mainstream OS](#mainstream-os) as long as it has Java `1.8.0_60` or higher installed.
2. Should be able to hold up to 1000 tasks without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands)
   should be able to accomplish most of the tasks faster using commands than using the mouse.
4. Should have a cloud based storage location for syncing between multiple devicies a user might have.
5. User interface should be uncluttered that displays tasks ordered by urgency and priority.
6. Should not need an installer.
7. Should work on Windows 7 or later.
8. Data should be stored locally in the form of a human editable text file.
9. Should work stand-alone, should not be a plug-in to another software.
10. Should be able to undo at least 10 most recent changes.

{More to be added}

## Appendix D : Glossary

##### Mainstream OS

> Windows, Linux, Unix, OS-X

##### Task

> A task has a deadline.

##### Floating task

> A task that does not have a deadline.

##### Completed task

> A task has been marked completed by user. Will not be shown in a list command.

## Appendix E : Product Survey

**Todoist**

Author: Tan Rei Yun

Pros:

* Can be accessed online and offline, through computers, mobile and tablet devices due to cloud storage.
* Allows collaborating on tasks with others.
* Supports recurring tasks.
* Allows setting of multiple priorities.
* Add a task using natural language.
* Allows for adding description to tasks.
* Reminders can be set for important tasks.
* Have a variety of keyboard shortcuts.

Cons:

* Cannot archive completed tasks.
* Heavy use of mouse needed.
* Most useful features are in premium version.
* Unable to set start time and end time to set aside a certain time block.

**Google Keep**

Author: Wang Ming Rui

Pros:

* Chrome integration
* Ability to pin important tasks
* Can create labels
* Can assign colours
* Can archive tasks
* Can take note with drawing
* Deleted tasks can be retrieved or deleted permamently
* Able to set a time for reminder
* Able to sync between mobile/desktop devices which shares google account
* Able to compose list of task
* Easy to use keyboard shortcuts
* Can invite colaborators

Cons:

* Search funciton does not have NLP integration
* Heavy use of mouse when adding additional features to task

**Trello**

Author: Mohamed Irfan

Pros:

* User can be more organise in completing his/her tasks
* User will not miss out on any tasks
* can be shared among multiple users
* useful for individuals and groups
* can be seen and edit across platforms

Cons:

* there are plenty of other task manager application out there
* will only be used by someone that is not lazy in managing their task
* only work if willing to constantly update and rearrange the task

**Iphone Calendar**

Author: Hu Zongqi

Pros:

* Able to remind me of event at set time and set frequency
* Can be viewed as a calendar and indicate differently for days with and without events

Cons:

* Does not have a diary function
* Cannot show all tasks in a list form
