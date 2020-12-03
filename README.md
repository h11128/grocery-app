# grocery-app

## overview
a grocery app that uses cardView, tablayout, volley, sqlite datebase. As some practice for android studio.

## issue 
volley can only receive JSONObject with strictly correct string mapping. I used Gson to convert object into jsonstring and convert it into JSONObject. Because Gson automatically escape several punctuation like [" :], I failed to post the object to api.
