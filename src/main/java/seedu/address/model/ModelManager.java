package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.JobContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Tag;
import seedu.address.model.wedding.Wedding;
import seedu.address.model.wedding.WeddingNameContainsKeywordsPredicate;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final WeddingBook weddingBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Wedding> filteredWeddings;

    /**
     * Initializes a ModelManager with the given addressBook, weddingBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs, ReadOnlyWeddingBook weddingBook) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.weddingBook = new WeddingBook(weddingBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredWeddings = new FilteredList<>(this.weddingBook.getWeddingList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs(), new WeddingBook());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    @Override
    public Path getWeddingBookFilePath() {
        return userPrefs.getWeddingBookFilePath();
    }

    @Override
    public void setWeddingBookFilePath(Path weddingBookFilePath) {
        requireNonNull(weddingBookFilePath);
        userPrefs.setWeddingBookFilePath(weddingBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    /**
     * Checks if the address book contains the exact instance of the specified person.
     * This method verifies whether the provided {@code person} object is equal to another
     * person in the address book
     *
     * @param person The person instance to check for in the address book.
     * @return {@code true} if the address book contains the exact instance of {@code person};
     *         {@code false} otherwise.
     * @throws NullPointerException if {@code person} is null.
     */
    public boolean hasExactPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasExactPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public void updateFilteredPersonList(JobContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== WeddingBook ================================================================================

    @Override
    public void setWeddingBook(ReadOnlyWeddingBook weddingBook) {
        this.weddingBook.resetData(weddingBook);
    }

    @Override
    public ReadOnlyWeddingBook getWeddingBook() {
        return weddingBook;
    }

    @Override
    public boolean hasWedding(Wedding wedding) {
        requireNonNull(wedding);
        return weddingBook.hasWedding(wedding);
    }

    @Override
    public void deleteWedding(Wedding target) {
        weddingBook.removeWedding(target);
    }

    @Override
    public void addWedding(Wedding wedding) {
        weddingBook.addWedding(wedding);
        updateFilteredWeddingList(PREDICATE_SHOW_ALL_WEDDINGS);
    }

    @Override
    public void setWedding(Wedding target, Wedding editedWedding) {
        requireAllNonNull(target, editedWedding);

        weddingBook.setWedding(target, editedWedding);
    }

    //=========== Filtered Wedding List Accessors ============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Wedding} backed by the internal list of
     * {@code versionedWeddingBook}
     */
    @Override
    public ObservableList<Wedding> getFilteredWeddingList() {
        return filteredWeddings;
    }

    @Override
    public void updateFilteredWeddingList(Predicate<Wedding> predicate) {
        requireNonNull(predicate);
        filteredWeddings.setPredicate(predicate);
    }

    @Override
    public void updateFilteredWeddingList(WeddingNameContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        filteredWeddings.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && weddingBook.equals(otherModelManager.weddingBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredWeddings.equals(otherModelManager.filteredWeddings);
    }

    //=========== Person, Wedding and Tag related accessors ============================================================

    /**
     * Gets a list of weddings whose name matches that of the tags in the set.
     *
     * @param model current Model containing the necessary wedding address book.
     * @param tags  Set of tags input by the user.
     * @return List of weddings that match the tag.
     */
    public List<Wedding> getWeddingFromTags(Model model, Set<Tag> tags) {
        List<String> predicate = tags
                .stream().map(Tag::getTagName).collect(Collectors.toList());
        List<Wedding> list = new ArrayList<>();

        for (Wedding wedding : model.getFilteredWeddingList()) {
            for (String tagName : predicate) {
                if (wedding.getWeddingName().toString().equals(tagName)) {
                    list.add(wedding);
                }
            }
        }

        return list;
    }

    /**
     * Updates the remaining list of weddings with the editedPerson.
     *
     * @param editedPerson Person whose new tags have been added to them.
     * @param personToEdit Person who has tags currently being added to them.
     * @param model        current Model containing necessary wedding address book.
     */
    public void updatePersonInWedding(Person editedPerson, Person personToEdit, Model model) {
        List<Wedding> weddingList = model.getFilteredWeddingList();

        List<Set<Person>> weddingParticipantsSet = weddingList.stream().map(Wedding::getParticipants)
                .toList();

        for (Set<Person> set : weddingParticipantsSet) {
            if (set.contains(personToEdit)) {
                set.remove(personToEdit);
                set.add(editedPerson);
            }
        }
    }

    /**
     * Removes the person from the participant list of weddings that correspond to the specified tag(s).
     *
     * @param editedPerson Person whose specified tags have been deleted from.
     * @param model        current Model containing necessary wedding address book.
     * @param editedTags   Set of tags that exist as a wedding as well.
     */
    public void deletePersonInWedding(Person editedPerson, Model model, Set<Tag> editedTags) {
        List<Wedding> weddingList = getWeddingFromTags(model, editedTags);

        List<Set<Person>> weddingParticipantsSet = weddingList.stream().map(Wedding::getParticipants)
                .toList();

        for (Set<Person> set : weddingParticipantsSet) {
            set.remove(editedPerson);
        }
    }
    /**
     * Removes all tags from a person and removes the person from any weddings related to those tags.
     *
     * @param personToEdit the person whose tags will be removed.
     * @param model        the current model containing the necessary wedding address book.
     */
    public Person personWithAllTagsRemoved(Person personToEdit, Model model) {
        Set<Tag> currentTags = new HashSet<>(personToEdit.getTags());

        deletePersonInWedding(personToEdit, model, currentTags);
        Person editedPerson = new Person(personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getEmail(), personToEdit.getAddress(), personToEdit.getJob(), Collections.emptySet());
        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return editedPerson;
    }

}
