#!/usr/bin/env python
from __future__ import print_function

from datetime import datetime
import requests
import sys
import getpass
import time
import argparse
import io
import json
import csv
import os
import datetime
import pickle
import shutil
import base64
import functools
from collections import namedtuple
from io import StringIO
from os.path import expanduser, join, exists
from os import remove
from prettytable import PrettyTable
from _collections import defaultdict


config_path= join(expanduser("~"),".skye-cli",".skye")
config_path_pageSize= join(expanduser("~"),".skye-cli",".pageSize")
if not exists(join(expanduser("~"), ".skye-cli")):
        os.mkdir(join(expanduser("~"), ".skye-cli"), 0o777) #0777-rwxrwxrwx
if exists(join(expanduser("~"), ".skye")):
    shutil.move(join(expanduser("~"), ".skye"), config_path)
if exists(join(expanduser("~"),".pageSize")):
    shutil.move(join(expanduser("~"), ".pageSize"), config_path_pageSize)


HoldingValues= defaultdict(list)

class Map(dict):
    """
    Example:
    m = Map({'first_name': 'Eduardo'}, last_name='Pool', age=24, sports=['Soccer'])
    """

    def __init__(self, *args, **kwargs):
        super(Map, self).__init__(*args, **kwargs)
        for arg in args:
            for k, v in arg.items():
                self[k] = v

        if kwargs:
            for k, v in kwargs.items():
                self[k] = v

    def __getattr__(self, attr):
        return self.get(attr)

    def __setattr__(self, key, value):
        self.__setitem__(key, value)

    def __setitem__(self, key, value):
        super(Map, self).__setitem__(key, value)
        self.__dict__.update({key: value})

    def __delattr__(self, item):
        self.__delitem__(item)

    def __delitem__(self, key):
        super(Map, self).__delitem__(key)
        del self.__dict__[key]


def eprint(*args, **kwargs):
    print(*args, file=sys.stderr, **kwargs)

def dict2obj(d):
    if isinstance(d, list):
        d = [dict2obj(x) for x in d]
    if not isinstance(d, dict):
        return d

    class C(object):
        pass

    o = C()
    for k in d:
        o.__dict__[k] = dict2obj(d[k])
    return o


def AddNewPair(Key, Pair):
    HoldingValues[Key].append(Pair)
    return HoldingValues


def AddToExisting(Key, Pair):
    HoldingValues[Key]=Pair
    return HoldingValues


def check_parameters():
    return True if len(sys.argv)!=3 else False #if Silent else Interactive


# Load up the current config
def check_config():
    if exists(config_path):
        with open(config_path) as data_file:
            return Map(json.load(data_file))
    else:
        eprint("Not logged in")
        sys.exit(1)


def check_config_pageSize():
    if exists (config_path_pageSize):
        with open(config_path_pageSize) as data_file:
            return json.load(data_file)


def task_create_helper(parameter_object):
    parameter_object.pop("type", None)


def task_post_create_helper(command_args, result_object):
    print("Task post create helper")
    while result_object['status'] not in ['ABORTED','WARNING','CANCELLED','FAILED','COMPLETED']:
        print("Waiting for task,  current status is "+result_object['status'])
        time.sleep(2)
        result_object = get_entity_instance(commands["taskStatus"],result_object["id"])
    print("Task ended with status : " + result_object['status'])


def read_attribute(attribute, entity, command_args, provided_value):
    config = check_config()
    check_parameter=check_parameters()
    if "fixed_values" in attribute:
        if provided_value is None:
            if check_parameter: #For Silent Mode
                provided_value =attribute["default"]
                if provided_value is not None:provided_value=provided_value.lower()
                return provided_value
            else:
                if "entity" in attribute: #For Interactive Mode
                    list_entity(commands[attribute["entity"]], command_args, config)
                    entity_id = input("Select the ID of " + attribute["title"] + " : ")
                    if entity_id=="":
                        entity_id=attribute["default"]
                        return entity_id
                    return get_entity_instance(commands[attribute["entity"]], entity_id)
                provided_value = input(attribute["title"] + " " + str(attribute["fixed_values"]) + " : ")
                if provided_value == "":
                    provided_value = attribute["default"]
                    return provided_value
                if provided_value is not None:provided_value=provided_value.lower()
                return provided_value

        else:
            if type(provided_value)==str:provided_value=provided_value.lower()
            if "entity" in attribute:
                return get_entity_instance(commands[attribute["entity"]],provided_value)
            return provided_value
    elif "entity" in attribute:
        if provided_value is None:
            list_entity(commands[attribute["entity"]], command_args, config)
            entity_id = input("Select the ID of " + attribute["title"] + " : ")
            return get_entity_instance(commands[attribute["entity"]],entity_id)
        else:
            return get_entity_instance(commands[attribute["entity"]],provided_value)
    else:
        return input(attribute["title"] + " : ") if provided_value is None else provided_value


def getselectedHeadersList():
      list = ['ID', 'Modified', 'Owner' ,'Ingested' ,'Destruction Time' ,'Path']
      return  list


def getselectedDataKeyList():
      list = ['id', 'lastModified', 'owner', 'ingested','destroyedOn','path']
      return  list


def createCSVFile(response,downLoadFilePath):
    output = io.StringIO()
    year = datetime.datetime.now().year
    output.write(str(year))
    month =datetime.datetime.now().month
    output.write(str(month))
    day = datetime.datetime.now().day
    output.write(str(day))
    hour = datetime.datetime.now().hour
    output.write("_")
    output.write(str(hour))
    min = datetime.datetime.now().minute
    output.write(str(min))
    second = datetime.datetime.now().second
    output.write(str(second))
    output.write(".csv")
    file_path = downLoadFilePath+os.sep+ output.getvalue();

    emp_data = response.json()["results"]
    # open a file for writing
    print("creating file......");

    employ_data = open(file_path, 'wb')

# create the csv writer object

    csvwriter = csv.writer(employ_data)
    headers  = getselectedHeadersList();
    keyList = getselectedDataKeyList();

    count = 0

    for emp in emp_data:
            myList = [];
            header = headers;
            if count == 0:
               csvwriter.writerow(header)
               count += 1
            for emp1 in keyList:
               if((emp1 == 'lastModified') or (emp1 == 'ingested') or (emp1 == 'destroyedOn' )):
                 newValue = time.strftime("%a, %d %b %Y %H:%M:%S +0000", time.localtime(emp[emp1]/1000))
                 #newValue = datetime.datetime.utcfromtimestamp(emp[emp1]/1000).strftime("%m/%d/%Y %H:%M:%S")
                 myList.insert(keyList.index(emp1),newValue)
               else:
                 myList.insert(keyList.index(emp1),emp[emp1])
            csvwriter.writerow(myList)
    employ_data.close()
    print("File name :"+file_path+" are created");


def resolve_parameters(command_args, sub_command):
    entity = command_args.command_meta
    args_as_dict = vars(command_args)
    parameter_object = {}
    if "create" in entity:
        for attribute in entity["create"]:
                attribute_value = read_attribute(
                    entity["attributes"][attribute], entity, command_args, args_as_dict[attribute])
                if attribute_value =='direct' or attribute_value=='staged':
                    attribute_value=attribute_value.upper()
                if command_args.json:
                    print(attribute_value)
                if "attribute" in entity["attributes"][attribute]:
                    parameter_object[entity["attributes"][attribute]["attribute"]] = attribute_value
                if attribute == "objectSetId":
                    parameter_object[attribute] = attribute_value["id"]
                else:
                 parameter_object[attribute] = attribute_value
                 #if(entity["title"] == "projectsOnhold"):
                        #getProjectOnHold(entity);
                 #if(entity["title"] == "destructionPreviewReport"):
                        #getProjectOnHold(entity);
                 #else:
                 if "type" in parameter_object:
                        if parameter_object["type"] == "explore":
                            if "businessFunction" in entity["create"]:         entity["create"].remove("businessFunction")
                            if "password" in entity["create"]:                 entity["create"].remove("password")
                            if("createSchema" in entity["create"]):			   entity["create"].remove("createSchema")
                            if "selectedArchiveQueuedDate" in entity["create"]:entity["create"].remove("selectedArchiveQueuedDate")
                            if "projectOrObjectSet" in entity["create"]:       entity["create"].remove("projectOrObjectSet")
                            if "objectSetId" in entity["create"]:             entity["create"].remove("objectSetId")
                            if "extractAllArchiveOfProject" in entity["create"]:entity["create"].remove("extractAllArchiveOfProject")
                            if "archiveMode" in entity["create"]:              entity["create"].remove("archiveMode")
                            if "compressRunAfterArchive" in entity["create"]:  entity["create"].remove("compressRunAfterArchive")
                            if "allProjects" in entity["create"]:              entity["create"].remove("allProjects")
                            if "informationStore" in entity["create"]:         entity["create"].remove("informationStore")
                            if "recursive" in entity["create"]:                entity["create"].remove("recursive")
                            if "externalLocationPath" in entity["create"]:    entity["create"].remove("externalLocationPath")
                            if "targetExtractStoreInstance" in entity["create"]:entity["create"].remove("targetExtractStoreInstance")
                            if "checkInfoStore" in entity["create"]:          entity["create"].remove("checkInfoStore")
                            if ("verifyAllArchive" in entity["create"]):        entity["create"].remove("verifyAllArchive")
                        elif (parameter_object["type"] == "destroy"):
                            if "businessFunction" in entity["create"]:        entity["create"].remove("businessFunction")
                            if "password" in entity["create"]:                entity["create"].remove("password")
                            if("createSchema" in entity["create"]):			  entity["create"].remove("createSchema")
                            if "selectedArchiveQueuedDate" in entity["create"]:entity["create"].remove("selectedArchiveQueuedDate")
                            if "projectOrObjectSet" in entity["create"]:      entity["create"].remove("projectOrObjectSet")
                            if "objectSetId" in entity["create"]:             entity["create"].remove("objectSetId")
                            if "extractAllArchiveOfProject" in entity["create"]:entity["create"].remove("extractAllArchiveOfProject")
                            if "archiveMode" in entity["create"]:             entity["create"].remove("archiveMode")
                            if "compressRunAfterArchive" in entity["create"]: entity["create"].remove("compressRunAfterArchive")
                            if attribute=="allProjects":
                                if parameter_object[attribute] == "true":
                                    parameter_object["project"] = None
                                    parameter_object["informationStoreDefinition"] = None
                                    entity["create"].remove("project")
                                    entity["create"].remove("informationStore")
                                parameter_object.pop("allProjects")
                            if "isOnlyBasicFileMetadataIndex" in entity["create"]:entity["create"].remove("isOnlyBasicFileMetadataIndex")
                            if "recursive" in entity["create"]:                   entity["create"].remove("recursive")
                            if "reindex" in entity["create"]:                     entity["create"].remove("reindex")
                            if "externalLocationPath" in entity["create"]:        entity["create"].remove("externalLocationPath")
                            if "targetExtractStoreInstance" in entity["create"]:  entity["create"].remove("targetExtractStoreInstance")
                            if "checkInfoStore" in entity["create"]:              entity["create"].remove("checkInfoStore")
                            if "verifyAllArchive" in entity["create"]:            entity["create"].remove("verifyAllArchive")
                        elif (parameter_object["type"] == "index"):
                            if "businessFunction" in entity["create"]:            entity["create"].remove("businessFunction")
                            if "password" in entity["create"]:                    entity["create"].remove("password")
                            if("createSchema" in entity["create"]):			   entity["create"].remove("createSchema")
                            if "selectedArchiveQueuedDate" in entity["create"]:   entity["create"].remove("selectedArchiveQueuedDate")
                            if "projectOrObjectSet" in entity["create"]:          entity["create"].remove("projectOrObjectSet")
                            if "objectSetId" in entity["create"]:                 entity["create"].remove("objectSetId")
                            if "extractAllArchiveOfProject" in entity["create"]:  entity["create"].remove("extractAllArchiveOfProject")
                            if "archiveMode" in entity["create"]:                 entity["create"].remove("archiveMode")
                            if "compressRunAfterArchive" in entity["create"]:     entity["create"].remove("compressRunAfterArchive")
                            if "allProjects" in entity["create"]:                 entity["create"].remove("allProjects")
                            if "informationStore" in entity["create"]:            entity["create"].remove("informationStore")
                            if "isOnlyBasicFileMetadataIndex" in entity["create"]:entity["create"].remove("isOnlyBasicFileMetadataIndex")
                            if "reindex" in entity["create"]:                     entity["create"].remove("reindex")
                            if "externalLocationPath" in entity["create"]:        entity["create"].remove("externalLocationPath")
                            if "targetExtractStoreInstance" in entity["create"]:  entity["create"].remove("targetExtractStoreInstance")
                            if "checkInfoStore" in entity["create"]:              entity["create"].remove("checkInfoStore")
                            if "verifyAllArchive" in entity["create"]:            entity["create"].remove("verifyAllArchive")
                        elif (parameter_object["type"] == "archive"):
                            if "hasNoBuisnessFunction" in HoldingValues:
                                if (HoldingValues["hasNoBuisnessFunction"][0] == "true"):
                                    if ("businessFunction" in entity["create"]):
                                        entity["create"].remove("businessFunction")
                            if "selectedArchiveQueuedDate" in entity["create"]:   entity["create"].remove("selectedArchiveQueuedDate")
                            if("createSchema" in entity["create"]):			   entity["create"].remove("createSchema")
                            if "projectOrObjectSet" in entity["create"]:          entity["create"].remove("projectOrObjectSet")
                            if "objectSetId" in entity["create"]:                 entity["create"].remove("objectSetId")
                            if "extractAllArchiveOfProject" in entity["create"]:  entity["create"].remove("extractAllArchiveOfProject")
                            if "allProjects" in entity["create"]:                 entity["create"].remove("allProjects")
                            if "isOnlyBasicFileMetadataIndex" in entity["create"]:entity["create"].remove("isOnlyBasicFileMetadataIndex")
                            if "recursive" in entity["create"]:                   entity["create"].remove("recursive")
                            if "reindex" in entity["create"]:                     entity["create"].remove("reindex")
                            if "externalLocationPath" in entity["create"]:        entity["create"].remove("externalLocationPath")
                            if "informationStore" in entity["create"]:                entity["create"].remove("informationStore")
                            if "businessFunction" in entity["create"]:                entity["create"].remove("businessFunction")
                            if "targetExtractStoreInstance" in entity["create"]:  entity["create"].remove("targetExtractStoreInstance")
                            if "checkInfoStore" in entity["create"]:              entity["create"].remove("checkInfoStore")
                            if "verifyAllArchive" in entity["create"]:            entity["create"].remove("verifyAllArchive")
                            if "projects" in HoldingValues:
                                if (parameter_object["project"]["encryption_type"] == "NONE"):
                                    if ("password" in entity["create"]):
                                        entity["create"].remove("password")
                        elif (parameter_object["type"] == "extract"):
                            if "projects" in HoldingValues:
                                if (parameter_object["project"]["encryption_type"] == "NONE"):
                                    if ("password" in entity["create"]):
                                        entity["create"].remove("password")
                            if "hasNoBuisnessFunction" in HoldingValues:
                                if (HoldingValues["hasNoBuisnessFunction"][0] == "true"):
                                    if ("businessFunction" in entity["create"]):
                                        entity["create"].remove("businessFunction")

                            if attribute=="informationStore":
                                if parameter_object[attribute]["implementation"]=="localFS":
                                    if "createSchema" in entity["create"]:
                                        entity["create"].remove("createSchema")
                                else:
                                    if "extractAllArchiveOfProject" in entity["create"]:
                                        entity["create"].remove("extractAllArchiveOfProject")

                            if attribute == "projectOrObjectSet":

                                if parameter_object[attribute]=="project":
                                    if ("project" in entity["create"]):
                                        entity["create"].remove("objectSetId")
                                        parameter_object.pop("projectOrObjectSet")

                                else:
                                    if "objectSetId" in entity["create"]:               entity["create"].remove("project")
                                    if "extractAllArchiveOfProject" in entity["create"]:entity["create"].remove("extractAllArchiveOfProject")
                                    if "selectedArchiveQueuedDate" in entity["create"]:entity["create"].remove("selectedArchiveQueuedDate")
                                    if "informationStore" in entity["create"]:         entity["create"].remove("informationStore")
                                    parameter_object.pop("projectOrObjectSet")

                            if attribute == "extractAllArchiveOfProject":
                                if parameter_object[attribute]== "true":
                                    if "selectedArchiveQueuedDate" in entity["create"]:
                                        entity["create"].remove("selectedArchiveQueuedDate")
                            # if ("password" in entity["create"]):
                            #     entity["create"].remove("password")
                            # if ("businessFunction" in entity["create"]):
                            #     entity["create"].remove("businessFunction")
                            if "archiveMode" in entity["create"]:                     entity["create"].remove("archiveMode")
                            if "compressRunAfterArchive" in entity["create"]:         entity["create"].remove("compressRunAfterArchive")
                            if "allProjects" in entity["create"]:                     entity["create"].remove("allProjects")
                            if "isOnlyBasicFileMetadataIndex" in entity["create"]:    entity["create"].remove("isOnlyBasicFileMetadataIndex")
                            if "reindex" in entity["create"]:                         entity["create"].remove("reindex")
                            if "recursive" in entity["create"]:                       entity["create"].remove("recursive")
                            if "checkInfoStore" in entity["create"]:                  entity["create"].remove("checkInfoStore")
                            if "verifyAllArchive" in entity["create"]:                entity["create"].remove("verifyAllArchive")
                        elif (parameter_object["type"] == "verify"):
                            if "businessFunction" in entity["create"]:                entity["create"].remove("businessFunction")
                            if "password" in entity["create"]:                        entity["create"].remove("password")
                            if "createSchema" in entity["create"]:			   entity["create"].remove("createSchema")
                            if "selectedArchiveQueuedDate" in entity["create"]:       entity["create"].remove("selectedArchiveQueuedDate")
                            if "projectOrObjectSet" in entity["create"]:              entity["create"].remove("projectOrObjectSet")
                            if "extractAllArchiveOfProject" in entity["create"]:      entity["create"].remove("extractAllArchiveOfProject")
                            if "archiveMode" in entity["create"]:                     entity["create"].remove("archiveMode")
                            if "compressRunAfterArchive" in entity["create"]:         entity["create"].remove("compressRunAfterArchive")
                            if "allProjects" in entity["create"]:                     entity["create"].remove("allProjects")
                            if "informationStore" in entity["create"]:                entity["create"].remove("informationStore")
                            if "isOnlyBasicFileMetadataIndex" in entity["create"]:    entity["create"].remove("isOnlyBasicFileMetadataIndex")
                            if "reindex" in entity["create"]:                         entity["create"].remove("reindex")
                            if "recursive" in entity["create"]:                       entity["create"].remove("recursive")
                            if "objectSetId" in entity["create"]:                     entity["create"].remove("objectSetId")

                            if type(attribute_value)==str:
                                case1 = "Verify archive store against the object metadata"
                                case2 = "Verify archive store against the original information source"
                                case3="Verify extract store against the archive source"
                                if "checkInfoStore" in entity["create"] and attribute_value.lower()==case1.lower():
                                    parameter_object[attribute] = "false"
                                    if "externalLocationPath" in entity["create"]:      entity["create"].remove("externalLocationPath")
                                    if "targetExtractStoreInstance" in entity["create"]:entity["create"].remove("targetExtractStoreInstance")

                                elif ("checkInfoStore" in entity["create"] and attribute_value.lower()== case2.lower()):
                                    parameter_object[attribute] = "true"
                                    if "targetExtractStoreInstance" in entity["create"]:entity["create"].remove("targetExtractStoreInstance")
                                    if "verifyAllArchive" in entity["create"]:          entity["create"].remove("verifyAllArchive")
                                elif ("checkInfoStore" in entity["create"] and attribute_value.lower()== case3.lower()):
                                    parameter_object[attribute] = "true"
                                    if "externalLocationPath" in entity["create"]:      entity["create"].remove("externalLocationPath")
                                    if "verifyAllArchive" in entity["create"]:          entity["create"].remove("verifyAllArchive")
                        elif parameter_object["type"] == "compress":
                            if "businessFunction" in entity["create"]:                entity["create"].remove("businessFunction")
                            if "password" in entity["create"]:                        entity["create"].remove("password")
                            if("createSchema" in entity["create"]):			   entity["create"].remove("createSchema")
                            if "archiveMode" in entity["create"]:                     entity["create"].remove("archiveMode")
                            if "compressRunAfterArchive" in entity["create"]:         entity["create"].remove("compressRunAfterArchive")
                            if "allProjects" in entity["create"]:                     entity["create"].remove("allProjects")
                            if "isOnlyBasicFileMetadataIndex" in entity["create"]:    entity["create"].remove("isOnlyBasicFileMetadataIndex")
                            if "reindex" in entity["create"]:                         entity["create"].remove("reindex")
                            if "recursive" in entity["create"]:                       entity["create"].remove("recursive")
                            if "externalLocationPath" in entity["create"]:            entity["create"].remove("externalLocationPath")
                            if "targetExtractStoreInstance" in entity["create"]:      entity["create"].remove("targetExtractStoreInstance")
                            if "checkInfoStore" in entity["create"]:                  entity["create"].remove("checkInfoStore")
                            if "verifyAllArchive" in entity["create"]:                entity["create"].remove("verifyAllArchive")
                            if "projectOrObjectSet" in entity["create"]:              entity["create"].remove("projectOrObjectSet")
                            if "extractAllArchiveOfProject" in entity["create"]:      entity["create"].remove("extractAllArchiveOfProject")
                            if "objectSetId" in entity["create"]:entity["create"].remove("objectSetId")

    return parameter_object


def get_entity_instance(entity,entity_id):
    config = check_config()
    HoldingValues = AddNewPair(entity.path, entity_id)
    if entity.path == "tasks/getArchiveQueuedDatesList/":
        project_id = HoldingValues["projects"]
        response = requests.get(config.url + '/api/1/' + entity.path +project_id[0],
                                headers={"x-api-token": config.api_key})
    else:
        response = requests.get(config.url + '/api/1/' + entity.path+"/"+entity_id,
                            headers={"x-api-token": config.api_key})

        if entity.path=="informationStoreDefinitions":
            if response.json()["implementation"]=="jdbcStructured":
                informationStore_id = HoldingValues["informationStoreDefinitions"]
                response1 = requests.get(config.url + '/api/1/businessfunction/' + informationStore_id[0] + '/businessFunctions',
                    headers={"x-api-token": config.api_key})
                AddNewPair("hasNoBuisnessFunction", "true") if len(response1.json()) == 0 else AddNewPair("hasNoBuisnessFunction","false")
            else:
                AddNewPair("hasNoBuisnessFunction", "true")

    if response.status_code == 200:
        if entity.path =="tasks/getArchiveQueuedDatesList/":
            return None if len(response.json())==0 else entity_id
        else:
            return response.json()
    else:
        print("Unable to find "+str(entity.singular)+" with ID "+str(entity_id)+" (status "+str(response.status_code)+")")
        print(response.text)
        sys.exit(1)


def list_entity(entity, command_args, config):
    choice = 'y'
    incrementPage = 0
    params = {}
    nextPage=True
    if command_args.page:
        config_path_pageSize = check_config_pageSize()
        params["_pageSize"] = command_args.pageSize if config_path_pageSize is None else config_path_pageSize["pageSize"]

    while choice.lower() == 'y' and nextPage :
        incrementPage = incrementPage + 1
        params["_page"] = incrementPage
        if entity.path=="tasks/getArchiveQueuedDatesList/":
            project_id = HoldingValues["projects"]
            response = requests.get(config.url + '/api/1/'+entity.path+project_id[0],params=params,
                headers={"x-api-token": config.api_key})
            if len(response.json())==0:
                print ("No archive Task completed successfully...")
                sys.exit(1)
        else:
            response = requests.get(config.url + '/api/1/' + entity.path, params=params,
                                    headers={"x-api-token": config.api_key})
        if entity.path=="businessfunction":
            informationStore_id = HoldingValues["informationStoreDefinitions"]
            response = requests.get(config.url + '/api/1/businessfunction/' + informationStore_id[0] + '/businessFunctions',
                headers={"x-api-token": config.api_key})

        if response.status_code == 200:
            if entity.help == "Manage archive Dates" or entity.help =="Manage businessfunction":
                response.results = response.json()
                response.totalResults = len(response.json())
                response.pageSize = response.totalResults
                response.page = 1
                data=response
            else:
                data = Map(response.json())
            print("No records found.") if data.totalResults is 0 else print("\n" + entity["title"] + "\n") if "title" in entity else print("\n")

            headers = []
            for attribute in entity.list:
                headers.append(entity.attributes[attribute]["title"])

            table = PrettyTable(headers)
            for row in data.results:
                values = []
                for attribute in entity.list:
                    if "path" in entity.attributes[attribute]:
                        try:
                            values.append(functools.reduce(dict.get, entity.attributes[
                                attribute]["path"].split("."), row))
                        except TypeError:
                            values.append("(null)")
                    else:
                        values.append(row[attribute])
                table.add_row(values)
            print(table)
            if data.pageSize ==0:
                data.pageSize = data.totalResults
            try:
                total_pages = data.totalResults / data.pageSize
                remainder = data.totalResults % data.pageSize
                if remainder ==0:

                    print("\nShowing page " + str(data.page) + " of " + str(total_pages) + " (" + str(
                    data.totalResults) + " total results)\n")
                else:
                    total_pages=total_pages+1
                    print("\nShowing page " + str(data.page) + " of " + str(total_pages) + " (" + str(
                        data.totalResults) + " total results)\n")
                if data.page<total_pages:
                    nextPage=True
                    choice = input("Do you want more(y/n):")
                else:
                    nextPage =False
            except ZeroDivisionError:
                nextPage=False


        else:
            print("Unsuccessful response,  response was " + str(response.status_code))
            sys.exit(1)



def manage_entity(command_args):
    entity = command_args.command_meta
    config = check_config()
    config_pageSize = check_config_pageSize()

    if command_args.sub_command == "list":
        list_entity(entity, command_args, config)
    if command_args.sub_command == "create":
        parameter_object = resolve_parameters(command_args, "create")
        if "updateOnHold" in parameter_object:
         if(parameter_object["updateOnHold"]) == "true":
            parameter_object['projects']['onHold']  = 'true'
         elif(parameter_object["updateOnHold"]) == "false":
            parameter_object['projects']['onHold']  = 'false'
         if(parameter_object['projects']['encryption_type'] == "NONE"):
             parameter_object['projects']['encrypted_val']  = '0'
         if(parameter_object['projects']['encryption_type'] == "AES"):
             parameter_object['projects']['encrypted_val']  = '1'
         if(parameter_object['projects']['encryption_type'] == "HPE"):
             parameter_object['projects']['encrypted_val']  = '2'
         url = config.url + '/api/1/' + entity.path + '/' +(parameter_object['projects'][u'id'])
         if command_args.create_helper is not None:
                command_args.create_helper(parameter_object)

         if command_args.json:
                print("Sending")
                print(json.dumps(parameter_object))

         response = requests.put(url, data=json.dumps(parameter_object["projects"]),
                                     headers={"x-api-token": config.api_key,"content-type":"application/json"})

        elif "previewDays" in parameter_object == True and "isSendMail" in parameter_object == False:
         if parameter_object["previewDays"]:
            url = config.url + '/api/1/' + entity.path + '/' +"destroyed_preview_CLI" + '/'+(parameter_object['projects'][u'id'])+'/'+parameter_object["previewDays"]
            if command_args.create_helper is not None:
                command_args.create_helper(parameter_object)

            if command_args.json:
                print("Sending")
                print(json.dumps(parameter_object))

            response = requests.get(url,headers={"x-api-token": config.api_key,"content-type":"application/json"})
        elif "previewDays" in parameter_object == True and "isSendMail" in parameter_object == True:
           if (parameter_object["previewDays"]):
                parameter_object["isSendMail"] = "True" if parameter_object["isSendMail"] == "1" else "False"
                parameter_object["configURL"]=config.url

                url = config.url + '/api/1/' + entity.path + '/' + "dispositoin_report"

                if command_args.create_helper is not None:
                    command_args.create_helper(parameter_object)

                if command_args.json:
                    print("Sending")
                    print(json.dumps(parameter_object))

                #response = requests.post(url,
                #                       headers={"x-api-token": config.api_key, "content-type": "application/json"})
                response = requests.post(url, data=json.dumps(parameter_object),
                                    headers={"x-api-token": config.api_key, "content-type": "application/json"})


        else :
            if "archiveMode" in parameter_object == True:
                if (parameter_object["archiveMode"] == ""):parameter_object["archiveMode"]="STAGED"
            if "compressRunAfterArchive" in parameter_object ==True:
                if(parameter_object["compressRunAfterArchive"] == ""):parameter_object["compressRunAfterArchive"] = "false"
                if parameter_object["project"]["archiveStoreInstance"]["implementation"]=='hitachi' or parameter_object["project"]["archiveStoreInstance"]["implementation"]=='s3':
                    parameter_object["compressRunAfterArchive"]="false"

            if "password" in parameter_object:
                try:
                    parameter_object["password"]=base64.b64decode(parameter_object["password"])
                    parameter_object["password"]=parameter_object["password"].decode('utf-8')
                except:
                    print("Invalid Password")
                    sys.exit(1)
            if "extractAllArchiveOfProject" in parameter_object:
                parameter_object.pop("informationStore")
                #url = config.url + '/api/1/' + entity.path + '/' + parameter_object["type"]
                #parameter_object.pop("type")
                if parameter_object["extractAllArchiveOfProject"] == "true":
                    isArchived=requests.get(config.url+'/api/1/'+entity.path+'/getArchiveQueuedDatesList/'+parameter_object['project']['id'],headers={"x-api-token": config.api_key})
                    if(len(isArchived.json())==0):
                        print ("No Archive task completed successfully...")
                        sys.exit(1)
                # response = requests.post(url, data=json.dumps(parameter_object),
                #                          headers={"x-api-token": config.api_key, "content-type": "application/json"})

            if "informationStoreDefinition" in parameter_object:
                a =parameter_object["informationStoreDefinition"]["id"]
                parameter_object.pop("informationStoreDefinition")
                parameter_object["informationStoreDefinition"]={}
                parameter_object["informationStoreDefinition"]["id"]=a
            if "node" in parameter_object:
                if parameter_object["node"]==None:
                    parameter_object["node"]={'id':'None'}

            url = config.url + '/api/1/' + entity.path + '/' + parameter_object["type"]

            if command_args.create_helper is not None:
                command_args.create_helper(parameter_object)

            if command_args.json:
                print("Sending")
                print(json.dumps(parameter_object))

            response = requests.post(url, data=json.dumps(parameter_object),
                        headers={"x-api-token": config.api_key, "content-type": "application/json"})

        if command_args.json:
            print("Recieved")
            print(response.json())

        if response.status_code == 200:
            if "previewDays" in parameter_object == True:
                if(parameter_object["previewDays"]) and "isSendMail" in parameter_object == False:
                    createCSVFile(response,parameter_object["filePath"])
                    #print("creating file......");
                    #print("getting destruction objects list in file");
                    #print("File path :" +parameter_object["filePath"]);
            else:
                    print("Created " + entity["singular"]+" with ID "+response.json()["id"])

            if command_args.post_create_helper is not None:
                    command_args.post_create_helper(command_args, response.json())
        else:
                print("Unsuccessful response,  response was " +
                      str(response.status_code))
                print(response.text)
                sys.exit(1)

    if command_args.sub_command == "get":
            instance = get_entity_instance(entity, command_args.id)
            headers = []
            for attribute in entity.list:
                headers.append(entity.attributes[attribute]["title"])

            table = PrettyTable(headers)
            values = []
            for attribute in entity.list:
                if "path" in entity.attributes[attribute]:
                    try:
                        values.append(functools.reduce(dict.get, entity.attributes[
                                  attribute]["path"].split("."), instance))
                    except TypeError:
                        values.append("(null)")
                else:
                    values.append(instance[attribute])
            table.add_row(values)
            print(table)


def logout(command_args):
    remove(config_path)
    print("Configuration deleted.")


def login(command_args):
    url = command_args.url
    email = command_args.email
    if not url:
        url = input(
            "Base URL [http://localhost:8090]: ") or r"http://localhost:8090"
    if not email:
        email = input(
            "Email address [admin@openskye.org]: ") or "admin@openskye.org"
    password = getpass.getpass()
    response = requests.get(url + '/api/1/account', auth=(email, password))
    if response.status_code == 200:
        config = {
            "url": url,
            "api_key": response.json()["apiKey"]
        }
        with open(config_path, "w") as text_file:
            text_file.write(json.dumps(config))
        print("Logged in successfully, saved config (" + config_path + ")")
    else:
        eprint("Login failed")
        sys.exit(1)


def page_size(command_args):
    check_config()
    global a
    a=command_args.pageSize
    config = {
        "pageSize": a,
    }
    with open(config_path_pageSize, "w") as text_file:
        text_file.write(json.dumps(config))


commands = Map({
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
    "pagination":
             Map({
                 "help":"Set up and Store credentials",
                 "function": page_size,
                 "help": "pagination",
                 "arguments":[
                     {
                         "name":"--pageSize",
                         "help":"The Page Size you want"
                     },
                 ]

             }),
    "logout":
        Map({
            "help": "Remove credentials",
            "function": logout
        }),
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
                },
                "onHold":
                    {
                        "title": "onHold"
                    }

            },
            "list": ["id", "domain", "archiveStore", "duplicationAllowed", "active", "onHold"],
            "pageable": True
        }),
    "archiveStores":
        Map({
            "singular": "archive store",
            "function": manage_entity,
            "help": "Manage archive stores",
            "path": "archiveStoreInstances",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "name": {
                    "title": "Name"
                },
                "implementation": {
                    "title": "Implementation"
                }
            },
            "list": ["id", "name", "implementation"],
            "pageable": True

        }),
    "informationStores":
        Map({
            "singular": "information store",
            "function": manage_entity,
            "help": "Manage information stores",
            "path": "informationStoreDefinitions",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "project": {
                    "title": "Project",
                    "path": "project.name"
                },
                "implementation": {
                    "title": "Implementation"
                },
                "name": {
                    "title": "Name"
                }
            },
            "list": ["id", "project", "name", "implementation"],
            "pageable": True
        }),
    "selectedArchiveQueuedDate":
       Map({
          "list" :["Archive Date"],
          "help" :"Manage archive Dates",
          "path" :"tasks/getArchiveQueuedDatesList/",
           "attributes":
               {
                "name":{
                    "title":"Name"
                }
               },
           "list":["name"],
          "function":manage_entity,
          "pageable":True
       }),
    "objectSetId":
        Map({
           "list":["Object Set Id"],
            "help":"Manage Object Set",
            "path":"objectSets",
            "attributes":
                {
                    "name":{
                        "title":"Name"
                    },
                    "id":{
                        "title":"Object Id"
                    },
                    "onHold":{
                        "title":"onHold"
                    }
                },
            "list":["name","id","onHold"],
            "function": manage_entity,
            "pageable": True
        }),
    "extractStores":
        Map({
            "singular": "extract store",
            "function": manage_entity,
            "help": "Manage extract stores",
            "path": "extractStoreInstances",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "name": {
                    "title": "Name"
                },
                "implementation": {
                    "title": "Implementation"
                }
            },
            "list": ["id", "name", "implementation"],
            "pageable": True
        }),
    "businessFunction":
        Map({
            "singular": "businessFunction",
            "function": manage_entity,
            "help": "Manage businessfunction",
            "path": "businessfunction",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "name": {
                    "title": "Name"
                },

            },
            "list": ["id", "name"],
            "pageable": True
        }),
    "tasks":
        Map({
            "singular": "task",
            "function": manage_entity,
            "create_helper" : task_create_helper,
            "post_create_helper": task_post_create_helper,
            "help": "Manage tasks",
            "path": "tasks",
            "title":"tasks",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "status": {
                    "title": "Status"
                },
                "allProjects":{
                    "title":"Destroy From All",
                    "fixed_values":["true","false"],
                    "default":"false"
                },
                "node": {
                    "fixed_values":"",
                    "title": "Assigned Node",
                    "path": "assignedNode.hostname",
                    "entity": "nodes",
                    "default": None
                },
                "step": {
                    "title": "Step",
                    "path": "stepLabel"
                },
                "project": {
                    "title": "Project",
                    "path": "project.name",
                    "entity": "projects"
                },
                "archiveStore": {
                    "title": "Archive Store",
                    "path": "project.archiveStoreInstance.name"
                },
                "informationStore": {
                    "title": "Information Store",
                    "path": "step.informationStoreDefinition.name",
                    "entity": "informationStores",
                    "attribute": "informationStoreDefinition"
                },
                "type": {
                    "title": "Task Type",
                    "fixed_values": ["archive","explore","index","verify","destroy","compress","extract"],
                    "path": "type"
                },
                "isOnlyBasicFileMetadataIndex":{
                    "title": "Explore Type Basic",
                    "fixed_values": ["true", "false"],
                    "path": "type",
                    "default": "true"
                },
                "reindex":{
                    "title": "Reindex Explore Task",
                    "fixed_values": ["true", "false"],
                    "path": "type",
                    "default" : "false"
                },
                "recursive":{
                    "title": "Index files inside containers (e.g. .zip, .tar)",
                    "fixed_values": ["true", "false"],
                    "path": "type",
                    "default": "false"
                },
                "checkInfoStore":{
                    "title": "Verification Type",
                    "fixed_values": ["Verify archive store against the object metadata", "Verify archive store against the original information source","Verify extract store against the archive source"],
                    "path": "type",
                    "default":"Verify archive store against the object metadata"
                },
                "externalLocationPath":{
                    "title": "Verify data replicated to an external location (path)",
                    "path": "type"
                },
                "targetExtractStoreInstance":{
                    "title": "Extract store",
                    "path": "step.extractStoreInstance.name",
                    "entity": "extractStores",
                    "attribute": "targetExtractStoreInstance"
                },
                "verifyAllArchive":{
                    "title": "Verify All Archived Objects",
                    "fixed_values": ["true", "false"],
                    "path": "type",
                    "default":"false"
                },
                "archiveMode": {
                    "title": "Mode",
                    "fixed_values": ["DIRECT","STAGED"],
                    "path": "archiveMode",
                    "default":"STAGED"
                },
                "compressRunAfterArchive": {
                    "title": "Run Compression After Archive",
                    "fixed_values": ["true","false"],
                    "path": "compressRunAfterArchive",
                    "default":"true"
                },
                "extractAllArchiveOfProject":{
                    "title":"Extract All Archive of Project",
                    "path":"type"
                },
                "objectSetId":{
                    "title":"Object ID",
                    "path":"objectSetId.name",
                    "entity":"objectSetId"
                },
                "projectOrObjectSet":{
                    "title":"Extract an object set or a project",
                    "path":"task.projectOrObjectSet.name"

                },
                "selectedArchiveQueuedDate":{
                    "title":"Select an Archive Queued date",
                    "path":"selectedArchiveQueuedDate.name",
                    "entity":"selectedArchiveQueuedDate"
                },
                "createSchema":{
                    "title": "Create new database Schema",
                    "fixed_values":["true","false"],
                    "default": "true"
                },
		"password":{
                    "title":"Enter password for Encryption",
                    "path" : "password"

                },
                "businessFunction":{
                    "title":"Select ID of Business Function",
                    "fixed_values":"",
                    "path" : "businessFunction.name",
                    "entity": "businessFunction",
                    "default": None

                }
            },

            "create": ["type","allProjects","projectOrObjectSet","objectSetId","isOnlyBasicFileMetadataIndex", "project", "node","informationStore",
                       "reindex","recursive","checkInfoStore","password","businessFunction","targetExtractStoreInstance","extractAllArchiveOfProject","createSchema",
                       "selectedArchiveQueuedDate","externalLocationPath","verifyAllArchive","archiveMode","compressRunAfterArchive"],
            "list": ["id", "status", "step", "project", "archiveStore", "informationStore", "node","selectedArchiveQueuedDate","objectSetId"],
            "pageable": True
        }),
    "projectsOnhold":
        Map({
            "singular": "onhold",
            "function": manage_entity,
            "help": "onHold projects",
            "path": "projects",
            "title": "projectsOnhold",
            "attributes": {
                "projects": {
                    "title": "Project",
                    "path": "project.name",
                    "entity": "projects",
                },
                 "updateOnHold":{
                                "title": "Update project on hold",
                                "fixed_values": ["true", "false"],
                                "path": "updateOnHold"
                            }

            },
            "create": ["projects","updateOnHold"],
            "pageable": True
        }),
      "destructionPreviewReport":
        Map({
            "singular": "destructionPreviewReport",
            "function": manage_entity,
            "help": "destruction Preview Report",
            "path": "objects",
            "title": "destructionPreviewReport",
            "attributes": {
                "projects": {
                    "title": "Project",
                    "path": "project.name",
                    "entity": "projects",
                },
                 "previewDays":{
                                "title": "Enter Preview Days",
                                "path": "previewDays"
                            },
                 "filePath":{
                                "title": "Enter path for creating destruction preview report file",
                                "path": "filePath"
                            }

            },
            "create": ["projects","previewDays","filePath"],
            "pageable": True
        }),
"dispositionPreviewReport":
        Map({
            "singular": "destructionPreviewReport",
            "function": manage_entity,
            "help": "destruction Preview Report",
            "path": "dispositionReportRecords",
            "title": "destructionPreviewReport",
            "attributes": {
                 "previewDays":{
                                "title": "Enter Preview Days",
                                "path": "previewDays"
                            },
                "isSendMail":{
                                "title": "Do you want to send Mail to stakeholder",
                                "fixed_values": ["0","1"],
                                "path": "isSendMail",
                                "default":0
                            }

            },
            "create": ["previewDays","isSendMail"],
            "pageable": True
        }),
    "nodes":
        Map({
            "function": manage_entity,
            "help": "Manage nodes",
            "path": "nodes",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "hostname": {
                    "title": "Hostname"
                },
                "serviceAccount": {
                    "title": "Service Account"
                }
            },
            "list": ["id", "hostname", "serviceAccount"],
            "pageable": True
        }),
        "taskStatus":
        Map({
            "function": manage_entity,
            "help": "Manage nodes",
            "path": "tasks/forStatus",
            "attributes": {
                "id": {
                    "title": "ID"
                },
                "status": {
                    "title": "Status"
                }
            },
            "pageable": True
        })
})

parent_parser = argparse.ArgumentParser(add_help=False)

parser = argparse.ArgumentParser(description='Skye CLI toolkit')
parser.add_argument('--version', action='version', version='SKYE-CLI VERSION : 3.1.4')
subparsers = parser.add_subparsers(help='Sub-command help')

for command in commands:
    command_parser = subparsers.add_parser(command, help=commands[command]["help"],
                                           parents=[parent_parser])



    if "function" in commands[command]:
        command_parser.set_defaults(
            command=command,
            command_meta=commands[command],
            func=commands[command]["function"],
            create_helper=commands[command]["create_helper"] if "create_helper" in commands[command] else None,
            post_create_helper=commands[command]["post_create_helper"] if "create_helper" in commands[command] else None)

    if "attributes" in commands[command]:
        action_subparsers = command_parser.add_subparsers(help='Actions help')
        list_parser = action_subparsers.add_parser("list", help="List",
                                                   parents=[parent_parser])
        list_parser.set_defaults(
            command=command,
            sub_command="list",
            command_meta=commands[command],
            func=commands[command]["function"])
        list_parser.add_argument(
            "--json", required=False, action='store_true', help="Show JSON")
        get_parser = action_subparsers.add_parser("get", help="Get",
                                                   parents=[parent_parser])
        get_parser.set_defaults(
            command=command,
            sub_command="get",
            command_meta=commands[command],
            func=commands[command]["function"])

        get_parser.add_argument(
            "--json", required=False, action='store_true', help="Show JSON")

        get_parser.add_argument(
            "id", help="The ID to get")

        if "create" in commands[command]:
            create_parser = action_subparsers.add_parser("create", help="Create",
                                                         parents=[parent_parser])
            create_parser.set_defaults(
                command=command,
                sub_command="create",
                command_meta=commands[command],
                func=commands[command]["function"])

            create_parser.add_argument(
                "--json", required=False, action='store_true', help="Show JSON")

            for value_parameter in commands[command]["create"]:
                create_parser.add_argument(
                    "--" + value_parameter, required=False, help="The value for " + value_parameter)

            if command is "tasks":
                create_parser.add_argument(
                    "--wait", required=False, action='store_true', help="Wait for the status of the task to become either 'ABORTED','WARNING','CANCELLED' or 'FAILED'")

    if "pageable" in commands[command]:
        command_parser.add_argument(
            "--page", help="The page number you wish to return", default=1)
        command_parser.add_argument(
            "--pageSize",help="The page size you wish to return", default=10)


    if "arguments" in commands[command]:
        for argument in commands[command]["arguments"]:
            command_parser.add_argument(
                argument["name"], help=argument["help"])

args = parser.parse_args()
args.func(args)
