package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.ChannelEmote;

public class ChannelEmoteMapper implements RowMapper<ChannelEmote> {

    @Override
    public ChannelEmote mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChannelEmote emote = new ChannelEmote();
        emote.setActive(rs.getBoolean("active"));
        emote.setAllowed(rs.getBoolean("allowed"));
        emote.setAllowGlobal(rs.getBoolean("allowglobal"));
        emote.setApprovalStatus(rs.getString("approvalstatus"));
        emote.setCreated(rs.getDate("created"));
        emote.setId(rs.getString("id"));
        emote.setLargeImageFilename(rs.getString("largeimagefilename"));
        emote.setLargeImageType(rs.getString("largeimagetype"));
        emote.setLargeImageUrl(rs.getString("largeimageurl"));
        emote.setLastUpdated(rs.getDate("lastupdated"));
        emote.setName(rs.getString("name"));
        emote.setPrefix(rs.getString("prefix"));
        emote.setSmallImageFilename(rs.getString("smallimagefilename"));
        emote.setSmallImageType(rs.getString("smallimagetype"));
        emote.setSmallImageUrl(rs.getString("smallimageurl"));
        emote.setSubOnly(rs.getBoolean("subonly"));
        emote.setTag(rs.getString("tag"));
        emote.setUserId(rs.getString("userid"));
        return emote;
    }

}
