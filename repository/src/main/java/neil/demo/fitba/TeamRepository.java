package neil.demo.fitba;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamRepository extends JpaRepository<Team, String> {
        
    @Query("SELECT t.name FROM Team t WHERE t.league = :league ORDER BY 1 ASC")
    public Iterable<String> findLeagueTeams(@Param("league") String league); 
    
    @Query("SELECT t.name, t.pts FROM Team t WHERE t.league= :league ORDER BY 2 DESC, 1 ASC")
    public Collection<?> findTeamAndPoints(@Param("league") String league);

}