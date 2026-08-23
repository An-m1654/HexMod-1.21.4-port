from __future__ import annotations
import json
import json5
from pathlib import Path
import re

langs = [
    "en_us",
    "ja_jp",
    "ru_ru",
    "zh_cn"
]

def main(lang: str):
    json5File: dict
    with open(Path(__file__).parent.parent.joinpath(f"Common/src/main/resources/assets/hexcasting/lang/{lang}.flatten.json5"), "r") as f:
        json5File = json5.loads(f.read())

    trackStack: list[str] = []

    for i in range(len(list(json5File.keys()))):
        trackStack.append(list(json5File.keys())[i])

    ori_result = {}

    while len(trackStack) != 0:
        current = trackStack.pop()
        parents = current.split("#")[:-1]
        key = current.split("#")[-1]

        currentPath = json5File
        for i in range(len(parents)):
            currentPath = currentPath[parents[i]]
        if type(currentPath[key]) == dict:
            for i in range(len(currentPath[key].keys())):
                trackStack.append(current + "#" + list(currentPath[key].keys())[i])
        elif type(currentPath[key] == str):
            ori_result[".".join(current.split("#")).replace("_.", "_").removesuffix(".").replace("/.", "/").replace(":.", ":")] = currentPath[key]

    result = {}
    for key in ori_result:
        val = ori_result[key]
        if (len(re.split("^block\\.", key)) >= 2 and len(re.split("^block\\.hexcasting\\.slate\\.", key)) <= 1):
            key = "item." + "".join(re.split("^block\\.", key)[1:])
            result[key] = val
        else:
            val = ori_result[key]
            result[key] = val

    with open(Path(__file__).parent.parent.joinpath(f"Common/src/main/resources/assets/hexcasting/lang/{lang}.json"), "w") as f:
        f.write(json.dumps(result, ensure_ascii=False, indent=4))

if __name__ == "__main__":
    for i in range(len(langs)):
        main(langs[i])