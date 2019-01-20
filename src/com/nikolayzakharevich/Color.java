package com.nikolayzakharevich;

public enum Color {
    BLUE {
        public String toString() {
            return "primary";
        }
    },
    WHITE {
        public String toString() {
            return "default";
        }
    },
    RED {
        public String toString() {
            return "negative";
        }
    },
    GREEN {
        public String toString() {
            return "positive";
        }
    }
}