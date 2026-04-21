package com.faceit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisStatsService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Ключи для Redis
    private static final String TEAM_MATCHES_PREFIX = "team:matches:";
    private static final String TEAM_WINS_PREFIX = "team:wins:";
    private static final String PLAYER_MATCHES_PREFIX = "player:matches:";
    private static final String PLAYER_KILLS_PREFIX = "player:kills:";
    private static final String PLAYER_ASSISTS_PREFIX = "player:assists:";
    private static final String PLAYER_DEATHS_PREFIX = "player:deaths:";
    private static final String PLAYER_WINS_PREFIX = "player:wins:";

    public void incrementTeamMatches(Integer teamId) {
        String key = TEAM_MATCHES_PREFIX + teamId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public void incrementTeamWins(Integer teamId) {
        String key = TEAM_WINS_PREFIX + teamId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public Long getTeamMatches(Integer teamId) {
        String key = TEAM_MATCHES_PREFIX + teamId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public Long getTeamWins(Integer teamId) {
        String key = TEAM_WINS_PREFIX + teamId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public BigDecimal getTeamWinRate(Integer teamId) {
        Long matches = getTeamMatches(teamId);
        Long wins = getTeamWins(teamId);
        if (matches == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(wins)
                .divide(BigDecimal.valueOf(matches), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void incrementPlayerMatches(Integer playerId) {
        String key = PLAYER_MATCHES_PREFIX + playerId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public void incrementPlayerWins(Integer playerId) {
        String key = PLAYER_WINS_PREFIX + playerId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public void incrementPlayerKills(Integer playerId, int kills) {
        String key = PLAYER_KILLS_PREFIX + playerId;
        redisTemplate.opsForValue().increment(key, kills);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public void incrementPlayerAssists(Integer playerId, int assists) {
        String key = PLAYER_ASSISTS_PREFIX + playerId;
        redisTemplate.opsForValue().increment(key, assists);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public void incrementPlayerDeaths(Integer playerId, int deaths) {
        String key = PLAYER_DEATHS_PREFIX + playerId;
        redisTemplate.opsForValue().increment(key, deaths);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }

    public Long getPlayerMatches(Integer playerId) {
        String key = PLAYER_MATCHES_PREFIX + playerId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public Long getPlayerWins(Integer playerId) {
        String key = PLAYER_WINS_PREFIX + playerId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public Long getPlayerKills(Integer playerId) {
        String key = PLAYER_KILLS_PREFIX + playerId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public Long getPlayerAssists(Integer playerId) {
        String key = PLAYER_ASSISTS_PREFIX + playerId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public Long getPlayerDeaths(Integer playerId) {
        String key = PLAYER_DEATHS_PREFIX + playerId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    public BigDecimal getPlayerKd(Integer playerId) {
        Long kills = getPlayerKills(playerId);
        Long deaths = getPlayerDeaths(playerId);
        if (deaths == 0) return kills > 0 ? BigDecimal.valueOf(kills) : BigDecimal.ZERO;
        return BigDecimal.valueOf(kills)
                .divide(BigDecimal.valueOf(deaths), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPlayerWinRate(Integer playerId) {
        Long matches = getPlayerMatches(playerId);
        Long wins = getPlayerWins(playerId);
        if (matches == 0) return BigDecimal.ZERO;
        return BigDecimal.valueOf(wins)
                .divide(BigDecimal.valueOf(matches), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }


    public Map<String, Object> getAllTeamStats(Integer teamId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("matchesPlayed", getTeamMatches(teamId));
        stats.put("matchesWon", getTeamWins(teamId));
        stats.put("winRate", getTeamWinRate(teamId));
        return stats;
    }

    public Map<String, Object> getAllPlayerStats(Integer playerId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("matchesPlayed", getPlayerMatches(playerId));
        stats.put("matchesWon", getPlayerWins(playerId));
        stats.put("kills", getPlayerKills(playerId));
        stats.put("assists", getPlayerAssists(playerId));
        stats.put("deaths", getPlayerDeaths(playerId));
        stats.put("kd", getPlayerKd(playerId));
        stats.put("winRate", getPlayerWinRate(playerId));
        return stats;
    }


    public void clearTeamStats(Integer teamId) {
        redisTemplate.delete(TEAM_MATCHES_PREFIX + teamId);
        redisTemplate.delete(TEAM_WINS_PREFIX + teamId);
    }

    public void clearPlayerStats(Integer playerId) {
        redisTemplate.delete(PLAYER_MATCHES_PREFIX + playerId);
        redisTemplate.delete(PLAYER_WINS_PREFIX + playerId);
        redisTemplate.delete(PLAYER_KILLS_PREFIX + playerId);
        redisTemplate.delete(PLAYER_ASSISTS_PREFIX + playerId);
        redisTemplate.delete(PLAYER_DEATHS_PREFIX + playerId);
    }

    // Синхронизация из PostgreSQL в Redis (при старте)
    public void syncTeamStats(Integer teamId, Long matches, Long wins) {
        if (matches > 0) {
            redisTemplate.opsForValue().set(TEAM_MATCHES_PREFIX + teamId, matches, 30, TimeUnit.DAYS);
        }
        if (wins > 0) {
            redisTemplate.opsForValue().set(TEAM_WINS_PREFIX + teamId, wins, 30, TimeUnit.DAYS);
        }
    }

    public void syncPlayerStats(Integer playerId, Long matches, Long wins, Long kills, Long assists, Long deaths) {
        if (matches > 0) {
            redisTemplate.opsForValue().set(PLAYER_MATCHES_PREFIX + playerId, matches, 30, TimeUnit.DAYS);
        }
        if (wins > 0) {
            redisTemplate.opsForValue().set(PLAYER_WINS_PREFIX + playerId, wins, 30, TimeUnit.DAYS);
        }
        if (kills > 0) {
            redisTemplate.opsForValue().set(PLAYER_KILLS_PREFIX + playerId, kills, 30, TimeUnit.DAYS);
        }
        if (assists > 0) {
            redisTemplate.opsForValue().set(PLAYER_ASSISTS_PREFIX + playerId, assists, 30, TimeUnit.DAYS);
        }
        if (deaths > 0) {
            redisTemplate.opsForValue().set(PLAYER_DEATHS_PREFIX + playerId, deaths, 30, TimeUnit.DAYS);
        }
    }
}
