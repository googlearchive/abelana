# Copyright 2015 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM grpc/java

## Adapt to the java jar name
ADD build/distributions/abelana-grpc-server-1.0/ /app

WORKDIR /app

RUN touch ~/logs

## Adapt to the java jar name
CMD /app/bin/abelana-grpc-server >> ~/logs 2>&1

EXPOSE 50051