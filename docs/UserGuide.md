---
layout: page
title: User Guide
---

## Introduction

**FirstImpressions** is a **desktop app for managing contacts, optimized for use via a Command Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI).

### Target Users
- **Team leaders and managers** who need to coordinate relationship professionals
- **Business professionals** who manage client relationships and appointments
- **Users comfortable with command-line interfaces** who prefer efficiency over graphical navigation

### User Proficiency Level
This application is designed for users who:
- Are comfortable with basic computer operations
- Can navigate using keyboard commands
- Have experience with command-line interfaces or are willing to learn
- Need efficient contact and appointment management tools

### What FirstImpressions Does
FirstImpressions helps you:
- **Manage your team of relationship professionals** by storing their contact information, skills, and specializations
- **Track client appointments and bookings** with detailed scheduling capabilities
- **Search and filter team members** by name, skills, or availability
- **Organize team members** with customizable tags and categories
- **Ensure perfect client-professional matches** through comprehensive search capabilities

With FirstImpressions, no client request is too hard to handle as our system is able to search through multiple preferences, ensuring the perfect match for our customers.

## Table of Contents

- [Introduction](#introduction)
  - [Target Users](#target-users)
  - [User Proficiency Level](#user-proficiency-level)
  - [What FirstImpressions Does](#what-firstimpressions-does)
- [Table of Contents](#table-of-contents)
- [Quick start](#quick-start)
  - [Prerequisites](#prerequisites)
  - [Installation Steps](#installation-steps)
- [Features](#features)
  - [Viewing help : `help`](#viewing-help--help)
  - [Adding a person: `add`](#adding-a-person-add)
  - [Adding a tag to an existing person: `add`](#adding-a-tag-to-an-existing-person-add)
  - [Listing all persons : `list`](#listing-all-persons--list)
  - [Editing a person : `edit`](#editing-a-person--edit)
  - [Locating team members by name: `find`](#locating-team-members-by-name-find)
  - [Deleting a person : `delete`](#deleting-a-person--delete)
  - [Clearing all entries : `clear`](#clearing-all-entries--clear)
  - [Assigning a booking : `book`](#assigning-a-booking--book)
  - [Exiting the program : `exit`](#exiting-the-program--exit)
- [Data Management](#data-management)
  - [Saving the data](#saving-the-data)
  - [Editing the data file](#editing-the-data-file)
- [FAQ](#faq)
- [Known issues](#known-issues)
- [Command summary](#command-summary)

--------------------------------------------------------------------------------------------------------------------

## Quick start

### Prerequisites

1. **Ensure you have Java 17 or above installed in your Computer.**
   
   <div markdown="span" class="alert alert-info">:information_source: **Java Installation Guide:**
   
   **Windows users:** Download Java 17 from [Oracle's official website](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or use [OpenJDK 17](https://adoptium.net/temurin/releases/?version=17).
   
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).
   
   **Linux users:** Install OpenJDK 17 using your package manager:
   - Ubuntu/Debian: `sudo apt install openjdk-17-jdk`
   - CentOS/RHEL: `sudo yum install java-17-openjdk-devel`
   - Arch Linux: `sudo pacman -S jdk17-openjdk`
   </div>

2. **Verify Java Installation:** <span id="verify-java"></span>
   Open a command terminal as follows: 
   - On Windows, press the Windows key on your keyboard, and type in cmd
   - On Mac, press the Command + Space keys at the same time, and type in terminal
   - On Linux, press the Control + Alt + T keys at the same time.

   Then in your terminal, run:
   ```
   java -version
   ```
   You should see output similar to:
   ```
   openjdk version "17.0.x" 2023-xx-xx
   OpenJDK Runtime Environment (build 17.0.x+x)
   OpenJDK 64-Bit Server VM (build 17.0.x+x, mixed mode, sharing)
   ```

### Installation Steps

1. **Download the latest `.jar` file** from [here](https://github.com/AY2526S1-CS2103T-T08-4/tp/releases).

2. **Copy the file** to the folder you want to use as the _home folder_ for the app.

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Do not move the .jar file after data has been created. The application stores data relative to the .jar file location. Moving it may cause loss of access to your data.
</div>

3. **Run the application:**
   
   - Open your command terminal, [as follows](#verify-java)
   - Navigate to the folder: `cd /path/to/your/folder`
   - Run: `java -jar firstimpressions.jar`

4. **Verify successful launch:** A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![defaultUI](images/defaultUI.jpg)
   <div markdown="span" class="alert alert-primary">:bulb: **Tip:** If the application doesn't start, ensure Java 17+ is properly installed and the .jar file is not corrupted.</div>

<div markdown="span" class="alert alert-warning">:exclamation: **Important:**
Always backup your `addressbook.json` file before making major changes. Data loss cannot be recovered without a backup.
</div>

5. **Try your first commands:** Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/Alice Tan p/98765432 e/alicetan@gmail.com t/vipHandler` : Adds a contact named `Alice Tan` to the list of contacts.

   * `delete n/Alice Tan` : Deletes the contact named Alice Tan from the whole list of contacts.

   * `clear f/` : Deletes all contacts.

   * `exit` : Exits the app.

6. **Explore more features:** Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/PERSON_NAME`, `PERSON_NAME` is a parameter which can be used as `add n/Alice Tan`.

* Items in square brackets are optional.<br>
  e.g `n/PERSON_NAME [t/TAG]` can be used as `n/Alice Tan t/vipHandler` or as `n/Alice Tan`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG​]…` can be used as ` ` (i.e. 0 times), `t/vipHandler`, `t/vipHandler t/teamLead` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, and `exit`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help : `help`

Shows a short summary of available commands, with a link to access the User Guide.

![help message](images/helpMessage.jpg)
*Figure 1: Help window showing available commands and User Guide link*

Format: `help`


### Adding a person: `add`

Adds a person to the contact list.

Format: `add n/NAME [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]…​`

* Consecutive spaces in name are removed. e.g. "Alice&nbsp;&nbsp;Tan" will be saved as `Alice Tan`.
* Names can contain letters, spaces, apostrophes, hyphens, and slashes. e.g. `s/o` (son of), `d/o` (daughter of).
* Phone numbers can contain any characters including `+`, `()`, `-`, spaces, and letters. Only whitespace-only phone numbers are not allowed.
* Tags must be alphanumeric and have no spaces.

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Duplicate names are not allowed. Each person in the contact list must have a unique name. If you try to add a person with an existing name, the command will fail.
</div>

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Tags must be alphanumeric only (no spaces, hyphens, or special characters). Use camelCase for multi-word tags (e.g., `vipHandler`, not `vip-handler` or `vip handler`).
</div>

<div markdown="span" class="alert alert-info">:information_source: **Character Limits:**
* Names: Maximum 100 characters
* Phone: Can contain any characters (numbers, +, (), -, spaces, letters, etc.), cannot be whitespace-only
* Email: Must be a valid email format
* Tags: Alphanumeric only, use camelCase for multi-word tags
</div>

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags (including 0). <br>
Use camelCase for multiword tags. e.g. teamLead
</div>

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
Phone numbers are flexible! You can use any format that works for you: international format with `+` (e.g., `+65 9123 4567`), with parentheses and hyphens (e.g., `(123) 456-7890`), or plain numbers (e.g., `98765432`). Extensions and additional text are also supported (e.g., `123-456 ext. 789`).
</div>

Examples:

* `add n/Alice Tan p/98702930 e/alicetan@gmail.com t/teamLead t/vipHandler` <br> will add a Person called `Alice Tan`, with phone number `98702930`, with email `alicetan@gmail.com`, with tags `teamLead` and `vipHandler` to the contact list.
* `add n/Bob Lee p/+65 9123 4567 t/mandarinSpeaking` <br> will add a Person called `Bob Lee` with phone number `+65 9123 4567` and tag `mandarinSpeaking` to the contact list.
* `add n/Abhijay s/o Abhi p/91234567 e/abhijay@example.com t/family` <br> will add a Person called `Abhijay s/o Abhi` (with slash for "son of") with tag `family` to the contact list.
* `add n/Charlie Wong p/(123) 456-7890` <br> will add a Person called `Charlie Wong` with phone number `(123) 456-7890` to the contact list.
* `add n/Germaine` <br> will add a Person called `Germaine` to the contact list.

![add message](images\addMessage.jpg)
*Figure 2: Success message after adding a new person to the contact list*

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
*Figure 3: Success message after adding tags to an existing person*

### Listing all persons : `list`

Shows the list of all persons in the contact list.

Format: `list`

![list message](images\listMessage.jpg)
*Figure 4: Contact list showing all persons in the address book*

### Editing a person : `edit`

Edits an existing person in the contact list.

Format: `edit n/OLD_NAME [n/NEW_NAME] [p/PHONE] [e/EMAIL] [t/TAG]…​`

* Edits the person identified by `OLD_NAME`. The name must exactly match (case-sensitive) a person in the contact list.
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* You can remove all the person's tags by typing `t/` without
    specifying any tags after it.

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Person names are case-sensitive. `edit n/john doe` will not edit `John Doe`. Use the exact name as shown in the contact list.
</div>

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
When editing tags, the existing tags of the person will be **completely replaced** (not added to). If you want to keep existing tags, you must include them in the edit command. For example, if a person has tags `teamLead` and `sales`, using `edit n/John t/manager` will replace both tags with only `manager`.
</div>

Examples:
*  `edit n/John Doe p/91234567 e/johntan@gmail.com` Edits the phone number and email address of the person named `John Doe` to be `91234567` and `johntan@gmail.com` respectively.
*  `edit n/Alice Tan p/+1 (555) 123-4567` Edits the phone number of `Alice Tan` to be `+1 (555) 123-4567`.
*  `edit n/John Doe n/Jane Doe t/` Edits the name of `John Doe` to be `Jane Doe` and clears all existing tags.

![edit message](images\editMessage.jpg)
*Figure 5: Success message after editing a person's details*

### Locating team members by name: `find`

Search and lists all people whose name contains the given parameter. <br>
If given a tag or date, will search all tags and bookings to list all people with an exact match.

Format: <br>
`find n/NAME` <br>
`find t/TAG1 TAG2...` <br>
`find d/DATE1 DATE2...` <br>

* Only one field is allowed, subsequent fields will be ignored. e.g. `find n/Hans t/teamLead` will find a person named `Hans t/teamLead`
* For "t/" and "d/", multiple parameters are allowed, all tags requested and all bookings with the requested dates will be listed. e.g. `find t/teamLead vipHandler` or `find d/2026-11-15 2026-12-25` will list all team member's that contain the requested parameters
* The search for names and tags are case-insensitive. e.g `hans` will match `Hans`
* Partial matches will be shown e.g. `find n/Ali` will find a person named `Alice`
* Date must be in `YYYY-MM-DD` format.

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
For "n/", only one parameter is allowed, subsequent parameters will be considered as part of the name. e.g. `find n/Ali n/Bob` will search for `Ali n/Bob`
</div>

Examples:
* `find n/John` returns `john` and `John Doe`
* `find d/2026-08-18` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.jpg)
  *Figure 6: Search results showing team members matching the search criteria*

### Deleting a person : `delete`

Remove a person from the contact list, or remove specific tag(s) from a person, or remove specific booking(s) from a person.

Format: `delete n/PERSON_NAME [t/TAG]…​ [b/BOOKING_INDEX]…​`

* If only `n/PERSON_NAME` is provided, then the person is removed from the contact list.
* If both `n/PERSON_NAME` and `t/TAG` is provided, then the specific tag(s) will be removed.
* If both `n/PERSON_NAME` and `b/BOOKING_INDEX` is provided, then the specific booking(s) will be removed.
* Only tags that currently belong to the person will be deleted.
* Booking indices refer to the booking number shown in the person's booking list (starting from 1).
* The name provided must exactly match (case-sensitive) a person in the contact list. e.g. `delete n/Alex` will not delete `Alex Yeoh`

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Person names are case-sensitive. `delete n/alice` will not delete `Alice Tan`. Use the exact name as shown in the contact list.
</div>

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
You cannot use both `t/` (tags) and `b/` (bookings) in the same delete command. The command will fail with: "Invalid command format! Only use either 'b/' or 't/', and not both!"
</div>

Examples:
* `delete n/Alex t/vipHandler` will remove the tag `vipHandler` from `Alex` in the contact list.
* `delete n/Alex Yeoh b/1` will remove the first booking from `Alex Yeoh` in the contact list.
* `delete n/Alex Yeoh` will remove `Alex Yeoh` from the contact list entirely. <br> 
  ![delete message](images\deleteMessage.jpg)
  *Figure 7: Success message after deleting a person from the contact list*

### Clearing all entries : `clear`

Clears all entries from the contact list.

Format: `clear f/`

* The command `clear` without the `f/` flag will display a warning message and will NOT delete any data.
* You must use `clear f/` to confirm and execute the deletion of all contacts and bookings.
* This two-step process helps prevent accidental data loss.

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
The `clear f/` command permanently deletes ALL contacts and bookings. This action cannot be undone. Make sure to backup your data before using this command.
</div>

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
If you accidentally type `clear`, don't worry! The system will show you a warning message instead of deleting your data. You must explicitly use `clear f/` to confirm the deletion.
</div>

Examples:
* `clear` - Shows a warning message without deleting any data
* `clear f/` - Clears all entries from the contact list

![clear message](images\clearMessage.jpg)
*Figure 8: Success message after clearing all entries from the contact list*

### Assigning a booking : `book`

Assigns a client meeting to a person at a given date and time, with an optional description.

Format: `book dt/DATETIME c/CLIENT_NAME n/PERSON_NAME [desc/DESCRIPTION]`

* Name of person provided must be in the current contact list.
* Client name can contain letters, spaces, apostrophes, hyphens, periods, and slashes. e.g. `s/o` (son of), `d/o` (daughter of).
* Datetime must be in `YYYY-MM-DD HH:MM` format in 24-hour notation.

<div markdown="span" class="alert alert-info">:information_source: **Time Format:**
Time must be in 24-hour format (HH:MM). Use `14:00` for 2:00 PM, `09:00` for 9:00 AM. Minutes are required even for on-the-hour times (e.g., `10:00`, not `10`).
</div>

<div markdown="span" class="alert alert-info">:information_source: **Past Dates:**
Bookings can be created for past dates, for record-keeping purposes. When booking a past date, a warning message will be displayed: "Note that this is a Booking that is in the past!" to remind you that this appointment has already occurred.

Additionally, past bookings will appear greyed out (with reduced opacity) in the contact list to visually distinguish them from future appointments.
</div>

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Dates must be valid calendar dates. Invalid dates like 2026-02-31 will be rejected. Always verify your date is correct before booking.
</div>

Examples:

* `book dt/2025-09-20 10:30 c/Madam Chen n/Bob Lee` will assign a booking on 20th September 2025 10.30am to Bob Lee. The client will be Madam Chen.
* `book dt/2025-10-18 14:00 c/Mr Lim n/Alice Tan desc/first consultation` will assign a booking on 18th October 2025 2pm to Alice Tan. The client will be Mr Lim and the description is "first consultation".
* `book dt/2020-10-26 17:00 c/Mr Lim n/Alice Yeoh desc/backdated` will create a booking for a past date (26th October 2020). <br>
  The result message will be: "Booked: Alice Yeoh with client 'Mr Lim' at 2020-10-26 17:00 [backdated]. Note that this is a Booking that is in the past!"
* `book dt/2025-11-15 15:00 c/Raj s/o Kumar n/Abhijay s/o Abhi desc/follow-up` will assign a booking on 15th November 2025 3pm to Abhijay s/o Abhi. The client Raj s/o Kumar (with slash for "son of") and the description is "follow-up". <br>
  ![book messsage](images\bookMessage.jpg)
  *Figure 9: Success message after creating a new booking*

<div markdown="span" class="alert alert-warning">:exclamation: **Warning:**
Double booking is not allowed. If you try to book the same person at the same time, the system will show an error message.
</div>

### Exiting the program : `exit`

Exits the program.

Format: `exit`

## Data Management

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
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous FirstImpressions home folder (your `addressbook.json` file from the `data` folder).

**Q**: Can I use FirstImpressions without an internet connection?<br>
**A**: Yes, FirstImpressions is a desktop application that works completely offline. All your data is stored locally on your computer.

**Q**: What happens if I accidentally delete a person?<br>
**A**: Unfortunately, there is no built-in undo feature. However, you can restore your data by copying a backup of your `addressbook.json` file from the `data` folder.

**Q**: What happens if I accidentally type `clear`?<br>
**A**: Don't worry! The system will show you a warning message without deleting any data. You must explicitly use `clear f/` with the force flag to confirm the deletion of all contacts.

**Q**: Can I import contacts from other applications?<br>
**A**: Currently, FirstImpressions does not support direct import from other applications. You would need to manually add contacts using the `add` command.

**Q**: Is there a limit to the number of contacts I can store?<br>
**A**: There is no hard limit, but performance may decrease with very large datasets (thousands of contacts). The application is optimized for typical business use cases.

**Q**: Can I customize the date and time format?<br>
**A**: No, the application uses a fixed format: `YYYY-MM-DD HH:MM` for dates and times. This ensures consistency across all users.

**Q**: What if I forget the exact name of a person when using delete or edit commands?<br>
**A**: Use the `find` command first to locate the person, then use the exact name shown in the results for delete or edit operations.

**Q**: Can I book multiple appointments for the same person at different times?<br>
**A**: Yes, you can book multiple appointments for the same person as long as they are at different times. The system prevents double booking at the same time.

**Q**: How do I backup my data?<br>
**A**: Simply copy the `addressbook.json` file from the `data` folder in your FirstImpressions directory. Store this backup in a safe location.

**Q**: What should I do if the application crashes or won't start?<br>
**A**: First, ensure Java 17+ is properly installed. If the problem persists, try deleting the `preferences.json` file and restarting the application. If data corruption is suspected, restore from a backup of your `addressbook.json` file.

**Q**: What phone number formats are supported?<br>
**A**: Phone numbers are very flexible in FirstImpressions! You can use any format including:
- Plain numbers: `98765432`
- International format: `+65 9123 4567`
- With parentheses and hyphens: `(123) 456-7890`
- With extensions: `123-456 ext. 789`
- Mixed format: `+1 (555) 123-4567`

The only restriction is that phone numbers cannot be empty or contain only whitespace.
If you do want an empty phone number, set the phone number to "-"!

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Help** | `help`
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL [t/TAG]…​` <br> e.g., `add n/Alice Tan p/98702930 e/alicetan@gmail.com t/teamLead t/vipHandler`
**List** | `list`
**Edit** | `edit n/OLD_NAME [n/NEW_NAME] [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]…​`<br> e.g.,`edit n/John Doe n/Jane Doe p/91234567 e/janedoe@example.com`
**Find** | `find n/NAME` or `find t/TAG1 t/TAG2...` or `find d/DATE1 d/DATE2...`<br> e.g., `find n/John` or `find d/2025-08-18`
**Delete** | `delete n/PERSON_NAME [t/TAG]…​ [b/BOOKING_INDEX]…​`<br> e.g., `delete n/Alex Yeoh` or `delete n/Alex t/vipHandler` or `delete n/Alex Yeoh b/1`
**Clear** | `clear f/`
**Book** | `book dt/DATETIME c/CLIENT_NAME n/PERSON_NAME [desc/DESCRIPTION]` <br> e.g., `book dt/2025-09-18 14:00 c/Mr Lim n/Alice Tan desc/first consultation`
**Exit** | `exit`
