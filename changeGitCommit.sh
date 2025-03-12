#!/bin/sh

git filter-branch --env-filter '

# 之前不规范的邮箱
OLD_EMAIL="yuruizhi@vchangyi.com" 

# 修改后的正确邮箱
CORRECT_EMAIL="282373647@qq.com"

if [ "$GIT_COMMITTER_EMAIL" = "$OLD_EMAIL" ]
then
	export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
fi
if [ "$GIT_AUTHOR_EMAIL" = "$OLD_EMAIL" ]
then
	export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
fi
' --tag-name-filter cat -- --branches --tags