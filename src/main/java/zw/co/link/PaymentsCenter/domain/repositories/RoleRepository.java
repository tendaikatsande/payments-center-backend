package zw.co.link.PaymentsCenter.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.link.PaymentsCenter.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}