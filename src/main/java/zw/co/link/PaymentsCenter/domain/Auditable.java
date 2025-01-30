package zw.co.link.PaymentsCenter.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable  {

    @CreatedDate
    private Instant createdDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Instant updatedDate;

    @LastModifiedBy
    private String updatedBy;

    private Instant deletedDate;

    private String deletedBy;


}