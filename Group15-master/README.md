# RestoApp Project - Group 15

## General Information

This is a repository for the RestoApp Application for Group 15 of the ECSE 223 Winter 2018 Course Offering.

## Workflow Description

A development branch will be created for any feature that is to be implemented.
[Click here to see all of our branches](https://github.com/W2018-ECSE223/Group15/branches)
For future reference the branches were created locally and pushed (creating the associated remote branch in the process).

**Please substitute imad-dev with your development branch name**

`git checkout master`

`git pull`

`git checkout -b imad-dev`

We then pushed it, linking it by setting the 'upstream' (remote link) to the origin remote repository. 

`git push --set-upstream origin imad-dev`

**Our work will proceed in the following way (FeatureBranch Workflow) ** 

Members will work on the branches created for the feature they are implementing, before commencing work on their feature, members should execute the following commands.

`git checkout imad-dev`

`git pull`

Members can then do their work, save the changed files and execute the following commands to push their work onto the remote github repository.

`git add *` (Or relevent files)

`git commit -m "Commit Message"`

`git push`

When you're done with the task you're trying to accomplish. Go to github and [Generate a Pull Request](https://github.com/W2018-ECSE223/Group15/compare?expand=1): **(new Pull Request on repository homepage)**

The base should be master and the compare should be your dev branch. 
Write a description of your pull request and link it the issue you're trying to solve by writing, for example, `#11` in the comment body. **Please Include a well-written description like "Implement create method for X functionality"** There may be some merge conflicts which you can either solve on your device or on the web-editor. If you are struggling with this, let someone know. 

**Let someone on the team review your code and then merge** 

Assign someone (or the whole team) as 'Reviewers' on your Pull Request and wait until your request is reviewed and approved by someone on the team. If further changes are needed before your Request can be merged, these are automatically added to your Pull Request by commiting and pushing the changes as you were doing before.

**If you need to update your branch with work that has been implemented on the master branch**

First commit and push the work you've already done

`git checkout master`

`git pull`

`git checkout imad-dev`

`git rebase master`

`git push`

After you are done working on your feature, you can delete the feature branch. If you want to work on a new feature (or fix bugs in an existing feature), create a new development branch from your local master (see above). 




