package zw.co.link.FinPay.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.link.FinPay.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}