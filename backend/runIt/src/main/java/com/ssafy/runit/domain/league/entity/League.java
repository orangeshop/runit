package com.ssafy.runit.domain.league.entity;

import com.ssafy.runit.domain.group.entity.Group;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "league")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class League {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String leagueName;


    @OneToMany(mappedBy = "groupLeague")
    private Set<Group> groups;

    public void updateGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    @Column(name = "league_rank")
    private long rank;
}
