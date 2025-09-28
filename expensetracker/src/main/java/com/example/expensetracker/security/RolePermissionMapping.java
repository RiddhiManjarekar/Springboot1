package com.example.expensetracker.security;

import com.example.expensetracker.util.constants.Authority;
import java.util.List;
import java.util.Map;

public class RolePermissionMapping {

    public static final Map<String, List<Authority>> ROLE_PERMISSIONS = Map.of(
        "USER", List.of(Authority.READ, Authority.WRITE),
        "ADMIN", List.of(Authority.READ, Authority.WRITE, Authority.UPDATE, Authority.DELETE, Authority.ADMIN)
    );
}
