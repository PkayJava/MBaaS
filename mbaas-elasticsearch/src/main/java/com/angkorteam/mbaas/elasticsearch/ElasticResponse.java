package com.angkorteam.mbaas.elasticsearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by socheat on 9/24/16.
 */
public class ElasticResponse implements Serializable {

    @Expose
    @SerializedName("_index")
    private String index;

    @Expose
    @SerializedName("_type")
    private String type;

    @Expose
    @SerializedName("_id")
    private String id;

    @Expose
    @SerializedName("_version")
    private Long version;

    @Expose
    @SerializedName("created")
    private Boolean created;

    @Expose
    @SerializedName("found")
    private Boolean found;

    @Expose
    @SerializedName("_shards")
    private Shards shards;

    @Expose
    @SerializedName("took")
    private Long took;

    @Expose
    @SerializedName("timed_out")
    private Boolean timedOut;

    @Expose
    @SerializedName("_source")
    private Object source;

    @Expose
    @SerializedName("hits")
    private Hits hits;

    public static class Hits implements Serializable {

        @Expose
        @SerializedName("total")
        private Long total;

        @Expose
        @SerializedName("max_score")
        private Double maxScore;

        @Expose
        @SerializedName("hits")
        private List<Hit> hits;

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Double getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(Double maxScore) {
            this.maxScore = maxScore;
        }

        public List<Hit> getHits() {
            return hits;
        }

        public void setHits(List<Hit> hits) {
            this.hits = hits;
        }
    }

    public static class Hit implements Serializable {

        @Expose
        @SerializedName("_index")
        private String index;

        @Expose
        @SerializedName("_type")
        private String type;

        @Expose
        @SerializedName("_id")
        private String id;

        @Expose
        @SerializedName("_score")
        private Double score;

        @Expose
        @SerializedName("_source")
        private Object source;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }
    }

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Boolean getFound() {
        return found;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }

    public Boolean getCreated() {
        return created;
    }

    public void setCreated(Boolean created) {
        this.created = created;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    public static class Shards implements Serializable {

        @Expose
        @SerializedName("total")
        private Long total;

        @Expose
        @SerializedName("successful")
        private Long successful;

        @Expose
        @SerializedName("failed")
        private Long failed;

        @Expose
        @SerializedName("created")
        private Boolean created;

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getSuccessful() {
            return successful;
        }

        public void setSuccessful(Long successful) {
            this.successful = successful;
        }

        public Long getFailed() {
            return failed;
        }

        public void setFailed(Long failed) {
            this.failed = failed;
        }

        public Boolean getCreated() {
            return created;
        }

        public void setCreated(Boolean created) {
            this.created = created;
        }

    }
}
