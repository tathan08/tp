---
layout: page
title: User Guide
---

FirstImpressions is a **desktop app for managing contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). We help leaders to keep track of their team of relationship professionals’ strengths, along with their schedules. With FirstImpressions, no client request is too hard to handle as our system is able to search through multiple preferences, ensuring the perfect match for our customers.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2526S1-CS2103T-T08-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for the app.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar firstimpressions.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![defaultUI](images/defaultUI.jpg)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/Alice Tan p/98765432 e/alicetan@gmail.com t/vip-handler` : Adds a contact named `Alice Tan` to the list of contacts.

   * `delete n/Alice Tan` : Deletes the contact named Alice Tan from the whole list of contacts.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

2. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/PERSON_NAME`, `PERSON_NAME` is a parameter which can be used as `add n/Alice Tan`.

* Items in square brackets are optional.<br>
  e.g `n/PERSON_NAME [t/TAG]` can be used as `n/Alice Tan t/vip-handler` or as `n/Alice Tan`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG​]…` can be used as ` ` (i.e. 0 times), `t/vip-handler`, `t/vip-handler t/team-lead` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help : `help`

Shows a short summary of available commands, with a link to access the User Guide.

![help message](images/helpMessage.jpg)

Format: `help`


### Adding a person: `add`

Adds a person to the contact list.

Format: `add n/NAME [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]…​`

* Consecutive spaces in name are removed. e.g. "Alice&nbsp;&nbsp;Tan" will be saved as `Alice Tan`.
* Phone numbers must be numeric only.
* Tags must be alphanumeric and have no spaces.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags (including 0). <br>
Use camelCase for multiword tags. e.g. teamLead
</div>

Examples:

* `add n/Alice Tan p/98702930 e/alicetan@gmail.com t/teamLead t/vipHandler` <br> will add a Person called `Alice Tan`, with phone number `98702930`, with email `alicetan@gmail.com`, with tags `teamLead` and `vipHandler` to the contact list.
* `add n/Bob Lee t/mandarinSpeaking` <br> will add a Person called `Bob Lee` with tag `mandarinSpeaking` to the contact list.
* `add n/Germaine` <br> will add a Person called `Germaine` to the contact list.

![add message](images\addMessage.jpg)

### Adding a tag to an existing person: `add`

Adds a tag to an existing person.

Format: `add n/NAME t/TAG…​`

* Consecutive spaces in name are removed. e.g. "Alice&nbsp;&nbsp;Tan" will be saved as `Alice Tan`.
* Multiple  tags can be added at once by prefixing each tag with `t/`.
* Tags must be alphanumeric and have no spaces.
* If person does not already exist in FirstImpressions, the person is created.
* Other tags e.g. `e/email@example.com` here will be ignored.

Examples:

* `add n/Alice Tan t/sales t/manager` <br> will add tags `sales` and `manager` to the Person called `Alice Tan`.

![add tags](images\addTags.png)

### Listing all persons : `list`

Shows the list of all persons in the contact list.

Format: `list`

![list message](images\listMessage.jpg)

### Editing a person : `edit`

Edits an existing person in the contact list.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the currently displayed person list. <br>
The index **must be a positive integer** 1, 2, 3, …​ and <br>
**must be between 1 and the total number of items in the currently displayed person list**
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 p/91234567 e/johntan@gmail.com` Edits the phone number and email address of the 1st person to be `91234567` and `johntan@gmail.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

![edit message](images\editMessage.jpg)

### Locating team members by name: `find`

Search and lists all people whose name contains the given parameter. <br>
If given a tag or date, will search all tags and bookings to list all people with an exact match.

Format: <br>
`find [n/NAME]` <br>
`find [t/TAG1] [t/TAG2]...` <br>
`find [d/DATE1] [d/DATE2]...` <br>

* Only one field is allowed, subsequent fields will be ignored. e.g. `find n/Hans t/teamLead` is equivalent to `find n/Hans`
* For "/n", only one parameter is allowed, subsequent parameters will be considered as part of the name. e.g. `find n/Ali n/Bob` will search for `Ali n/Bob`
* For "t/" and "d/", multiple parameters are allowed, all tags requested and all bookings with the requested dates will be listed. e.g. `find t/teamLead vipHandler` or `find d/2026-11-15 2026-12-25` will list all team member's that contain the requested parameters
* The search for names and tags are case-insensitive. e.g `hans` will match `Hans`
* Partial matches will be shown e.g. `find n/Ali` will find a person named `Alice`
* Date must be in `YYYY-MM-DD` format.

Examples:
* `find n/John` returns `john` and `John Doe`
* `find d/2026-08-18` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.jpg)

### Deleting a person : `delete`

Remove a person from the contact list, or remove specific tag(s) from a person.

Format: `delete n/PERSON_NAME [t/TAG]…​`

* If only `n/PERSON_NAME` is provided, then the person is removed from the contact list.
* If both `n/PERSON_NAME` and `t/TAG` is provided, then the specific tag(s) will be removed.
* Only tags that currently belong to the person will be deleted.
* The name provided must exactly match (case-sensitive) a person in the contact list. e.g. `delete n/Alex` will not delete `Alex Yeoh`

Examples:
* `delete n/Alex t/vipHandler` will remove the tag `vipHandler` from `Alex` in the contact list.
* `delete n/Alex Yeoh` will remove `Alex Yeoh` from the contact list. <br> ![delete message](images\deleteMessage.jpg)

### Clearing all entries : `clear`

Clears all entries from the contact list.

Format: `clear`

![clear message](images\clearMessage.jpg)

### Assigning a booking : `book`

Assigns a client meeting to a person at a given date and time, with an optional description.

Format: `book dt/DATETIME c/CLIENT_NAME n/PERSON_NAME [desc/DESCRIPTION]`

* Name of person provided must be in the current contact list.
* Datetime must be in `YYYY-MM-DD HH:MM` format in 24-hour notation.

Examples:

* `book dt/2025-09-20 10:30 c/Madam Chen n/Bob Lee` will assign a booking on 20th September 2025 10.30am to Bob Lee. The client will be Madam Chen.
* `book dt/2025-10-18 14:00 c/Mr Lim n/Alice Tan desc/first consultation` will assign a booking on 18th October 2025 2pm to Alice Tan. The client will be Mr Lim and the description is "first consultation". <br>
  ![book messsage](images\bookMessage.jpg)

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

FirstImpressions data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

FirstImpressions data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes to the data file makes its format invalid, FirstImpressions will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the FirstImpressions to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous FirstImpressions home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Help** | `help`
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL [t/TAG]…​` <br> e.g., `add n/Alice Tan p/98702930 e/alicetan@gmail.com t/team-lead t/vip-handler`
**List** | `list`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find [n/NAME] [t/TAG] [d/DATE]`<br> e.g., `find d/2025-08-18`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Clear** | `clear`
**Book** | `book dt/DATETIME c/CLIENT_NAME n/PERSON_NAME [desc/DESCRIPTION]` <br> e.g., `book dt/2025-09-18 14:00 c/Mr Lim n/Alice Tan desc/first consultation`
**Exit** | `exit`
