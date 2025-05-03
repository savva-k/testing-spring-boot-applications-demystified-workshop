package pragmatech.digital.workshops.lab1.service;

import org.springframework.stereotype.Service;
import pragmatech.digital.workshops.lab1.domain.LibraryUser;
import pragmatech.digital.workshops.lab1.util.TimeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing library users.
 */
@Service
public class UserService {
    
    private final Map<String, LibraryUser> users = new HashMap<>();
    private final TimeProvider timeProvider;
    
    public UserService(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }
    
    /**
     * Register a new library user.
     * 
     * @param name the name of the user
     * @param email the email of the user
     * @return the created user
     */
    public LibraryUser registerUser(String name, String email) {
        String id = UUID.randomUUID().toString();
        String membershipNumber = generateMembershipNumber();
        
        LibraryUser user = new LibraryUser(id, name, email, membershipNumber, timeProvider.getCurrentDate());
        users.put(id, user);
        return user;
    }
    
    /**
     * Find a user by ID.
     * 
     * @param id the ID of the user
     * @return an Optional containing the user, if found
     */
    public Optional<LibraryUser> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
    
    /**
     * Find a user by email.
     * 
     * @param email the email of the user
     * @return an Optional containing the user, if found
     */
    public Optional<LibraryUser> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    /**
     * Find a user by membership number.
     * 
     * @param membershipNumber the membership number of the user
     * @return an Optional containing the user, if found
     */
    public Optional<LibraryUser> findByMembershipNumber(String membershipNumber) {
        return users.values().stream()
                .filter(user -> user.getMembershipNumber().equals(membershipNumber))
                .findFirst();
    }
    
    /**
     * Find all users.
     * 
     * @return a list of all users
     */
    public List<LibraryUser> findAllUsers() {
        return new ArrayList<>(users.values());
    }
    
    /**
     * Update a user's information.
     * 
     * @param id the ID of the user to update
     * @param name the new name
     * @param email the new email
     * @return the updated user
     */
    public LibraryUser updateUser(String id, String name, String email) {
        LibraryUser user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        
        user.setName(name);
        user.setEmail(email);
        return user;
    }
    
    /**
     * Delete a user.
     * 
     * @param id the ID of the user to delete
     */
    public void deleteUser(String id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " not found");
        }
        users.remove(id);
    }
    
    /**
     * Generate a unique membership number.
     * 
     * @return a membership number
     */
    private String generateMembershipNumber() {
        // For simplicity, just generate a random number and prefix with LIB-
        return "LIB-" + (1000 + (int) (Math.random() * 9000));
    }
}