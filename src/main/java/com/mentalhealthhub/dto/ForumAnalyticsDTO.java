package com.mentalhealthhub.dto;

import java.util.List;

public class ForumAnalyticsDTO {
    private Long totalPosts;
    private Long totalComments;
    private Double averageViewsPerPost;
    private Double averageRepliesPerPost;
    private List<CategoryStatsDTO> categoryStats;
    private List<TopPostDTO> topPosts;
    private List<UserParticipationDTO> activeUsers;

    public ForumAnalyticsDTO() {}

    // Getters and Setters
    public Long getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(Long totalPosts) {
        this.totalPosts = totalPosts;
    }

    public Long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Long totalComments) {
        this.totalComments = totalComments;
    }

    public Double getAverageViewsPerPost() {
        return averageViewsPerPost;
    }

    public void setAverageViewsPerPost(Double averageViewsPerPost) {
        this.averageViewsPerPost = averageViewsPerPost;
    }

    public Double getAverageRepliesPerPost() {
        return averageRepliesPerPost;
    }

    public void setAverageRepliesPerPost(Double averageRepliesPerPost) {
        this.averageRepliesPerPost = averageRepliesPerPost;
    }

    public List<CategoryStatsDTO> getCategoryStats() {
        return categoryStats;
    }

    public void setCategoryStats(List<CategoryStatsDTO> categoryStats) {
        this.categoryStats = categoryStats;
    }

    public List<TopPostDTO> getTopPosts() {
        return topPosts;
    }

    public void setTopPosts(List<TopPostDTO> topPosts) {
        this.topPosts = topPosts;
    }

    public List<UserParticipationDTO> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<UserParticipationDTO> activeUsers) {
        this.activeUsers = activeUsers;
    }

    // Inner class for category stats
    public static class CategoryStatsDTO {
        private String category;
        private Long postCount;

        public CategoryStatsDTO(String category, Long postCount) {
            this.category = category;
            this.postCount = postCount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Long getPostCount() {
            return postCount;
        }

        public void setPostCount(Long postCount) {
            this.postCount = postCount;
        }
    }

    // Inner class for top posts
    public static class TopPostDTO {
        private Long id;
        private String title;
        private Integer views;
        private Integer replies;

        public TopPostDTO(Long id, String title, Integer views, Integer replies) {
            this.id = id;
            this.title = title;
            this.views = views;
            this.replies = replies;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getViews() {
            return views;
        }

        public void setViews(Integer views) {
            this.views = views;
        }

        public Integer getReplies() {
            return replies;
        }

        public void setReplies(Integer replies) {
            this.replies = replies;
        }
    }

    // Inner class for user participation
    public static class UserParticipationDTO {
        private Long userId;
        private String userName;
        private Long postCount;

        public UserParticipationDTO(Long userId, String userName, Long postCount) {
            this.userId = userId;
            this.userName = userName;
            this.postCount = postCount;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Long getPostCount() {
            return postCount;
        }

        public void setPostCount(Long postCount) {
            this.postCount = postCount;
        }
    }
}
