# skye-cli

A small Python based client to allow you to interact with the ISA REST interface.  

The script itself is based on argparse (https://docs.python.org/3/library/argparse.html) to parse the command line arguments and then requests (http://docs.python-requests.org/en/master/) to make the calls to the REST API.

The actual script parses out the command that you want to provide it into a command and a subcommand,  and then uses a very simple structure in the python to understand which entities are available that you can update.

## How does the CLI work?

Using the argparse library we set-up a list of the available commands:

https://github.com/infobelt/skye-cli/blob/master/skye.py#L426

These commands are defined in a map

https://github.com/infobelt/skye-cli/blob/master/skye.py#L426

The examples are things like login, or they can also be entity based like projects,  the metadata for each command is defined in a large map:

    "login":
        Map({
            "help": "Setup and store credentials",
            "function": login,
            "arguments":
                [
                    {
                        "name": "--url",
                        "help": "The URL of Skye API"
                    },
                    {
                        "name": "--email",
                        "help": "The email to use for login"
                    },
                ]
        }),
        
For something like the login command you can see that we provide help text etc - and also the parameters that you can send, and in this case we name the _function_ within the script that will be called.

When we are dealing with a command like projects, we use a command function (manage_entity) and define metadata to determine the structure of the entity you want to handle:

        "projects":
        Map({
            "singular": "project",
            "function": manage_entity,
            "help": "Manage projects",
            "path": "projects",
            "title": "Projects",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "active": {
                    "title": "Is Active"
                },
                "duplicationAllowed": {
                    "title": "Is Duplication Allowed"
                },
                "archiveStore":
                    {
                        "title": "Archive Store",
                        "path": "archiveStoreInstance.name"
                },
                "domain":
                    {
                        "title": "Domain",
                        "path": "domain.name"
                }

            },
            "list": ["id", "domain", "archiveStore", "duplicationAllowed", "active"],
            "pageable": True
        }),
        
The *manage_entity* function within the script is able to understand this metadata and provide screens for listing, creating and editing the entities.


## Building the CLI

Since we wanted deliver the CLI in a compiled form,  typically you would create VM (typically we use Vagrant) using a Centos 6/7 - based on if the client is a RHEL 6/7 user.  Once you the VM with the correct OS then you would check out the code to that VM, and install Python 2.

Then go to the checked out code and run:


    pip install pyinstaller
    build.sh
    
This should download the dependencies for the CLI and then compile that all into a single executable file (called skye-cli), which in the past we have then checked back into git (though we don't really need to)
    
