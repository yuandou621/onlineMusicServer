package dao;

import entity.Music;
import util.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MusicDao {
    /**
     * 查询全部歌单
     */
    public List<Music> findMusic() {
        List<Music> musicList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "select * from music";
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()){
                Music music = new Music();
                music.setId(rs.getInt("id"));
                music.setTitle(rs.getString("title"));
                music.setSinger(rs.getString("singer"));
                music.setTime(rs.getDate("time"));
                music.setUrl(rs.getString("url"));
                music.setUserid(rs.getInt("userid"));
                musicList.add(music);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,rs);
        }
        return musicList;
    }
    /**
     * 根据id查找音乐
     */
    public Music findMusicById(int id){
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Music music = null;

        try{
            String sql = "select * from music where id = ?";
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement(sql);

            ps.setInt(1,id);

            rs = ps.executeQuery();

            if(rs.next()){
                music = new Music();
                music.setId(rs.getInt("id"));
                music.setTitle(rs.getString("title"));
                music.setSinger(rs.getString("singer"));
                music.setTime(rs.getDate("time"));
                music.setUrl(rs.getString("url"));
                music.setUserid(rs.getInt("userid"));
            }

        }catch(SQLException e){
            e.printStackTrace();

        }finally {
            DBUtils.getClose(connection,ps,rs);

        }
        return music;
    }

    /**
     * 根据关键字查询歌单
     * @param str
     * @return
     */
    public List<Music> ifMusic(String str){
        List<Music> musics = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("select* from music where title like '%"+str+"%'");
            rs = ps.executeQuery();
            while(rs.next()) {
                Music music = new Music();
                music.setId(rs.getInt("id"));
                music.setTitle(rs.getString("title"));
                music.setSinger(rs.getString("singer"));
                music.setTime(rs.getDate("time"));
                music.setUrl(rs.getString("url"));
                music.setUserid(rs.getInt("userid"));
                musics.add(music);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            DBUtils.getClose(connection, ps, rs);
        }
        return musics;

    }

    /**
     * 上传音乐
     * @param title
     * @param singer
     * @param time
     * @param url
     * @param userid
     * @return
     */
    public int insert(String title, String singer, String time, String url,
                      int userid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int ret = 0;

        try {
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("insert into music(title, singer, time, url, userid) values(?,?,?,?,?)");

            ps.setString(1,title);
            ps.setString(2,singer);
            ps.setString(3,time);
            ps.setString(4,url);
            ps.setInt(5,userid);
            ret = ps.executeUpdate();

            return ret;

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,null);
        }
        return ret;
    }

    /**
     * 删除歌曲
     * @param id
     * @return
     */
    public int deleteMusicById(int id) {
        Connection connection = null;
        PreparedStatement ps = null;

        int ret = 0;
        try{
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("delete from music where id = ?");
            ps.setInt(1,id);
            ret = ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,null);
        }
        return ret;
    }
    /**
     *删除歌曲，需要先看该歌曲之前是否被添加到了，喜欢的音乐列表当中
     */
    public boolean findLoveMusicOnDel(int id) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("select * from lovemusic where music_id = ?");
            ps.setInt(1,id);
            rs =  ps.executeQuery();

            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,rs);
        }
        return false;
    }
    /**
     * 当删除服务器上的音乐时，同时在我喜欢的列表的数据库中进行删除。
     * @param musicId
     * @return
     */
    public int removeLoveMusicOnDelete(int musicId) {
        Connection connection = null;
        PreparedStatement ps = null;
        int ret = 0;
        try{
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("delete from lovemusic where music_id = ?");
            ps.setInt(1,musicId);
            ret = ps.executeUpdate();
            if(ret == 1){
                return ret;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,null);
        }
        return ret;

    }
    /**
     * 添加音乐到“喜欢”列表中
     * 用户-》音乐
     * 多对多
     * 需要中间表
     */
    public boolean insertLoveMusic(int userId,int musicId) {
        Connection connection = null;
        PreparedStatement ps = null;

        try{
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("insert into lovemusic(user_id, music_id) values (?,?)");
            ps.setInt(1,userId);
            ps.setInt(2,musicId);
            int ret = ps.executeUpdate();
            if(ret == 1){
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            DBUtils.getClose(connection,ps,null);
        }
        return false;
    }
    /**
     * @param userId 用户id
     * @param musicId 歌曲id
     * @return 返回受影响的行数
     * 移除当前用户喜欢的这首音乐，因为同一首音乐可能多个用户喜欢
     */
    public int removeLoveMusic(int userId,int musicId) {
        Connection connection = null;
        PreparedStatement ps = null;
        int ret = 0;
        try{
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("delete from lovemusic where user_id = ? and music_id = ?");
            ps.setInt(1,userId);
            ps.setInt(2,musicId);
            ret = ps.executeUpdate();
            if(ret == 1){
                return ret;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,null);
        }
        return ret;
    }
    /**
     * 添加喜欢的音乐的时候，需要先判断该音乐是否存在，也就是说，该用户是否之前添加过这个音乐为喜欢。
     * @param musicID
     * @return
     */
    public boolean findMusicByMusicId(int user_id,int musicID) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("select * from lovemusic where user_id =? and music_id =?");
            ps.setInt(1,user_id);
            ps.setInt(2,musicID);
            rs =  ps.executeQuery();

            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,rs);
        }
        return false;
    }
    /**
     * 查询该用户喜欢的全部歌单,只查询一张lovemusic是做不到的,需要联表查询。
     * @param user_id
     * @return
     */
    public List<Music> findLoveMusic(int user_id){
        List<Music> musics = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("select m.id as music_id,title,singer,time,url,userid from lovemusic lm,music m where lm.music_id=m.id and user_id = ?");
            ps.setInt(1,user_id);
            rs = ps.executeQuery();
            while(rs.next()){
                Music music = new Music();
                music.setId(rs.getInt("music_id"));
                music.setTitle(rs.getString("title"));
                music.setSinger(rs.getString("singer"));
                music.setTime(rs.getDate("time"));
                music.setUrl(rs.getString("url"));
                music.setUserid(rs.getInt("userid"));
                musics.add(music);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,rs);
        }
        return musics;
    }
    /**
     * 根据关键字查询该用户喜欢的某个音乐
     * @param str
     * @return
     */
    public List<Music> ifMusicLove(String str,int user_id){
        List<Music> musics = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            connection = DBUtils.getConnection();
            ps = connection.prepareStatement("select m.id as music_id,title,singer,time,url,userid from lovemusic lm,music m where lm.music_id=m.id and user_id = ? and title like '%"+str+"%'");
            ps.setInt(1,user_id);
            rs = ps.executeQuery();
            while(rs.next()){
                Music music = new Music();
                music.setId(rs.getInt("music_id"));
                music.setTitle(rs.getString("title"));
                music.setSinger(rs.getString("singer"));
                music.setTime(rs.getDate("time"));
                music.setUrl(rs.getString("url"));
                music.setUserid(rs.getInt("userid"));
                musics.add(music);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            DBUtils.getClose(connection,ps,rs);
        }
        return musics;
    }
}


