package simple.chatgpt.controller.management;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    // 🔎 LIST / SEARCH
    @GetMapping
    public ResponseEntity<Response<PagedResult<UserManagementPojo>>> searchUsers(
            @RequestParam Map<String, String> params
    ) {
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "20"));
        int offset = page * size;

        params.put("offset", String.valueOf(offset));
        params.put("limit", String.valueOf(size));
        params.put("sortField", params.getOrDefault("sortField", "id"));
        params.put("sortDirection", params.getOrDefault("sortDirection", "asc"));

        PagedResult<UserManagementPojo> users = userManagementService.searchUsers(params);
        return ResponseEntity.ok(new Response<>(users, "Fetched successfully"));
    }

    // 📖 READ (Flexible key)
    @GetMapping("/get")
    public ResponseEntity<Response<UserManagementPojo>> getUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userKey
    ) {
        UserManagementPojo user;
        if (id != null) {
            user = userManagementService.getUserById(id);
        } else if (userName != null) {
            user = userManagementService.getByUserName(userName);
        } else if (userKey != null) {
            user = userManagementService.getByUserKey(userKey);
        } else {
            throw new IllegalArgumentException("At least one key must be provided");
        }
        return ResponseEntity.ok(new Response<>(user, "Fetched successfully"));
    }

    // ➕ CREATE
    @PostMapping("/create")
    public ResponseEntity<Response<UserManagementPojo>> createUser(@RequestBody UserManagementPojo user) {
        UserManagementPojo created = userManagementService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new Response<>(created, "User created successfully"));
    }

    // ✏️ UPDATE (Flexible key)
    @PutMapping("/update")
    public ResponseEntity<Response<UserManagementPojo>> updateUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userKey,
            @RequestBody UserManagementPojo user
    ) {
        UserManagementPojo updated;
        if (id != null) {
            updated = userManagementService.updateUserById(id, user);
        } else if (userName != null) {
            updated = userManagementService.updateUserByUserName(userName, user);
        } else if (userKey != null) {
            updated = userManagementService.updateUserByUserKey(userKey, user);
        } else {
            throw new IllegalArgumentException("At least one key must be provided for update");
        }
        return ResponseEntity.ok(new Response<>(updated, "User updated successfully"));
    }

    // 🗑 DELETE (Flexible key)
    @DeleteMapping("/delete")
    public ResponseEntity<Response<Void>> deleteUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userKey
    ) {
        if (id != null) {
            userManagementService.deleteUserById(id);
        } else if (userName != null) {
            userManagementService.deleteUserByUserName(userName);
        } else if (userKey != null) {
            userManagementService.deleteUserByUserKey(userKey);
        } else {
            throw new IllegalArgumentException("At least one key must be provided for delete");
        }
        return ResponseEntity.ok(new Response<>(null, "User deleted successfully"));
    }
}
