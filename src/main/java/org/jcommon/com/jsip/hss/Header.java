// ========================================================================
// Copyright 2012 leolee<workspaceleo@gmail.com>
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//     http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================
package org.jcommon.com.jsip.hss;



/** Header is the base Class for all SIP Headers
 */
public class Header
{
   /** The header type */
   protected String name;
   /** The header string, without terminating CRLF */
   protected String value;

   /** Creates a void Header. */
   public Header()
   {  name=null;
      value=null;
   }

   /** Creates a new Header. */
   public Header(String hname, String hvalue)
   {  name=hname;
      value=hvalue;
   }

   /** Creates a new Header. */
   public Header(Header hd)
   {  name=hd.getName();
      value=hd.getValue();
   }

   /** Creates and returns a copy of the Header */
   public Object clone()
   {  return new Header(getName(),getValue());
   }

   /** Whether the Header is equal to Object <i>obj</i> */
   public boolean equals(Object obj)
   {  try
      {  Header hd=(Header)obj;
         if (hd.getName().equals(this.getName()) && hd.getValue().equals(this.getValue())) return true;
         else return false;
      }
      catch (Exception e) {  return false;  }
   }

   /** Gets name of Header */
   public String getName()
   {  return name; 
   }

   /** Gets value of Header */
   public String getValue()
   {  return value;
   }

   /** Sets value of Header */
   public void setValue(String hvalue)
   {  value=hvalue; 
   }

   /** Gets string representation of Header */
   public String toString()
   {  return name+": "+value+"\r\n";
   }
}
